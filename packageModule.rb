#!/usr/bin/env ruby

require 'optparse'

options = {
  :server => 'demo.fedarch.org',
  :port   => 80,
  :key    => nil,
  :name   => nil,
  :appid  => nil
}
opts = OptionParser.new do |opts|
  opts.banner = "Usage: #{$0} [options]"

  opts.on("-sSERVER","--server=SERVER", "Server for community APK to configure by default\n\t\t\t\t\t(Default: demo.fedarch.org)") do |server|
    options[:server] = server
  end
  opts.on("-pPORT","--port=PORT", "Port on the server to connect to\n\t\t\t\t\t(Default: 80)") do |port|
    options[:port] = port
  end
  opts.on("-kKEY","--key=KEY", "UUID of the module to download") do |modkey|
    options[:key] = modkey
  end
  opts.on("-nNAME","--name=NAME", "Name to display for the application") do |appname|
    options[:name] = appname
  end
  opts.on("-aAPPID","--appid=APPID", "Single word to append to the application's namespace\n\t\t\t\t\t(eg 'customfaims' will result in 'au.edu.faims.mq.customfaims')") do |appid|
    options[:appid] = "au.edu.faims.mq.#{appid}"
  end

end
opts.parse!

if !options[:key] || !options[:name] || !options[:appid]
  puts opts.help
  exit 1
end
if !(/[\p{XDigit}]{8}-[\p{XDigit}]{4}-[34][\p{XDigit}]{3}-[89ab][\p{XDigit}]{3}-[\p{XDigit}]{12}/ =~ options[:key])
  puts "Module key is not a valid UUID\n\n"
  puts opts.help
  exit 1
end
open("module.properties","w") {
  |fd|
  output = <<EOF
server="#{options[:server]}"
port="#{options[:port]}"
key="#{options[:key]}"
appname="#{options[:name]}"
appid=#{options[:appid]}
EOF
  fd.write(output)
  fd.sync()
  fd.close
}
puts "Cleaning project"
system("./gradlew clean > #{options[:appid]}.log 2>&1")
puts "Building project"
system("./gradlew build >> #{options[:appid]}.log 2>&1")
puts "Copying APK"
system("./gradlew publishAll >> #{options[:appid]}.log 2>&1")
puts "Done"
