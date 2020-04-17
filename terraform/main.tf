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
  instance_type               = "t2.micro"
  iam_instance_profile        = aws_iam_instance_profile.amaze_backend_profile.name
  associate_public_ip_address = true
  vpc_security_group_ids      = [
    aws_security_group.maze-group.id
  ]

  user_data = data.template_file.user_data.rendered
}

resource "aws_default_vpc" "default_vpc" {}

data "aws_subnet_ids" "default_subnets" {
  vpc_id = aws_default_vpc.default_vpc.id
}

resource "aws_security_group" "maze-group" {
  name   = "a-maze-backend-security-group"
  vpc_id = aws_default_vpc.default_vpc.id

  ingress {
    protocol  = "tcp"
    from_port = 80
    to_port   = 80
    self      = true
  }

  ingress {
    protocol    = "tcp"
    from_port   = 443
    to_port     = 443
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
  name               = "amaze-role"
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
  alias {
    evaluate_target_health = false
    name                   = aws_lb.load_balancer.dns_name
    zone_id                = aws_lb.load_balancer.zone_id
  }
  zone_id = "Z0805165AQE5BVWDG5L3"
  name    = "maze.andreamazzarella.com"
  type    = "A"
}

resource "aws_lb" "load_balancer" {
  name               = "maze-load-balancer"
  internal           = false
  idle_timeout       = 3600
  load_balancer_type = "application"
  security_groups    = [
    aws_security_group.maze-group.id
  ]
  subnets            = data.aws_subnet_ids.default_subnets.ids
}

resource "aws_lb_listener" "listener" {
  load_balancer_arn = aws_lb.load_balancer.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = "arn:aws:acm:eu-west-1:556103293623:certificate/0d0849fc-aeb8-47fa-862f-1966daf0102a"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.target_group.arn
  }
}

resource "aws_lb_target_group" "target_group" {
  name     = "maze-target-group"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_default_vpc.default_vpc.id
}

resource "aws_lb_target_group_attachment" "target_attachment" {
  target_group_arn = aws_lb_target_group.target_group.arn
  target_id        = aws_instance.maze-backend.id
  port             = 80
}
