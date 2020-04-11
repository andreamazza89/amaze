terraform {
  backend "s3" {
    bucket = "a-maze-terraform-state"
    key    = "all-the-things.tfstate"
    region = "eu-west-1"
  }
}

provider "aws" {
  region  = "eu-west-1"
  version = "~> 2.0"
}

data "template_file" "user_data" {
  template = file("${path.module}/launch.tpl")
  vars     = {
    version_tag = var.version_tag
  }
}

resource "aws_instance" "maze-backend" {
  ami                         = "ami-06ce3edf0cff21f07"
  instance_type               = "t2.small"
  iam_instance_profile        = aws_iam_instance_profile.amaze_backend_profile.name
  associate_public_ip_address = true
  vpc_security_group_ids      = [
    aws_security_group.maze-group.id
  ]

  user_data = data.template_file.user_data.rendered
}

resource "aws_default_vpc" "default_vpc" {}

resource "aws_security_group" "maze-group" {
  name   = "a-maze-backend-security-group"
  vpc_id = aws_default_vpc.default_vpc.id

  ingress {
    protocol  = "tcp"
    from_port = 80
    to_port   = 80
    cidr_blocks = [
      "0.0.0.0/0"
    ]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = [
      "0.0.0.0/0"
    ]
  }
}

resource "aws_iam_instance_profile" "amaze_backend_profile" {
  name = "a_maze_profile"
  role = aws_iam_role.amaze.name
}

resource "aws_iam_role" "amaze" {
  name = "amaze-role"
  assume_role_policy = data.aws_iam_policy_document.amaze_assume_role.json
}

data "aws_iam_policy_document" "amaze_assume_role" {
  statement {
    actions = [
      "sts:AssumeRole",
    ]

    principals {
      type = "Service"

      identifiers = [
        "ec2.amazonaws.com",
      ]
    }
  }
}

resource "aws_iam_role_policy_attachment" "can_pull_from_ecr" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  role       = aws_iam_role.amaze.id
}


resource "aws_route53_record" "maze" {
  zone_id = "Z0805165AQE5BVWDG5L3"
  name    = "maze.andreamazzarella.com"
  type    = "A"
  ttl     = "60"
  records = [aws_instance.maze-backend.public_ip]
}
