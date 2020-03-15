require 'net/http'
require 'json'

def post(address, query)
  uri = URI(address + "/graphql")
  request = Net::HTTP::Post.new(uri, 'Content-Type' => 'application/json')
  request.body = {query: query}.to_json
  response = Net::HTTP.start(uri.hostname, uri.port) do |http|
    http.request(request)
  end
  JSON.parse(response.body)
end

server_address = 'http://localhost:8080'

query = 'query {' \
        '  sampleQuery {' \
        '    aString' \
        '  }' \
        '}'

puts(post(server_address, query)['data']['sampleQuery'])

