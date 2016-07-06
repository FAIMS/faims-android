#!/usr/bin/env ruby

require 'optparse'

options = {
  :server => 'demo.fedarch.org',
  :port   => 80,
  :key    => nil,
  :name   => nil,
  :appid  => nil,
  :icon   => nil
}
opts = OptionParser.new do |opts|
  opts.banner = "Usage: #{$0} [options]"

  opts.on("-sSERVER","--server=SERVER", "Server for community APK to configure by default\n\t\t\t\t\t(Default: demo.fedarch.org)") do |server|
    options[:server] = server
  end
  opts.on("-pPORT","--port=PORT", "Port on the server to connect to\n\t\t\t\t\t(Default: 80)") do |port|
    options[:port] = port
  end
  opts.on("-kKEY","--key=KEY", "UUID of the module to download (required)") do |modkey|
    options[:key] = modkey
  end
  opts.on("-nNAME","--name=NAME", "Name to display for the application (required)") do |appname|
    options[:name] = appname
  end
  opts.on("-aAPPID","--appid=APPID", "Single word to append to the application's namespace (required)\n\t\t\t\t\t(eg 'customfaims' will result in 'au.edu.faims.mq.customfaims')") do |appid|
    options[:appid] = "au.edu.faims.mq.#{appid}"
  end
  opts.on("-iICON","--icon=ICON", "Path to the icon image to use for this package (required)") do |appicon|
    options[:icon] = appicon
  end

end
opts.parse!

if !options[:key] || !options[:name] || !options[:appid] || !options[:icon]
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
def scaleImage(image)
  system("convert #{image} -filter spline -resize 96x -background transparent -gravity center -extent 96x96 -unsharp 0x1 ./faimsandroidapp/src/main/res/drawable-xhdpi/ic_launcher.png")
  system("convert #{image} -filter spline -resize 96x -background transparent -gravity center -extent 96x96 -unsharp 0x1 ./faimsandroidapp/src/main/res/drawable/ic_launcher.png")
  system("convert #{image} -filter spline -resize 72x -background transparent -gravity center -extent 72x72 -unsharp 0x1 ./faimsandroidapp/src/main/res/drawable-hdpi/ic_launcher.png")
  system("convert #{image} -filter spline -resize 48x -background transparent -gravity center -extent 48x48 -unsharp 0x1 ./faimsandroidapp/src/main/res/drawable-mdpi/ic_launcher.png")
  system("convert #{image} -filter spline -resize 36x -background transparent -gravity center -extent 36x36 -unsharp 0x1 ./faimsandroidapp/src/main/res/drawable-ldpi/ic_launcher.png")
end
puts "Generating icons"
scaleImage(options[:icon])
puts "Cleaning project"
system("./gradlew clean > #{options[:appid]}.log 2>&1")
puts "Building project"
system("./gradlew build >> #{options[:appid]}.log 2>&1")
puts "Copying APK"
system("./gradlew publishAll >> #{options[:appid]}.log 2>&1")
puts "Done"

#drawable-xhdpi/ic_launcher.png: PNG image data, 96 x 96, 8-bit/color RGBA, non-interlaced
#drawable/ic_launcher.png:       PNG image data, 72 x 72, 8-bit/color RGBA, non-interlaced
#drawable-hdpi/ic_launcher.png:  PNG image data, 72 x 72, 8-bit/color RGBA, non-interlaced
#drawable-mdpi/ic_launcher.png:  PNG image data, 48 x 48, 8-bit/color RGBA, non-interlaced
#drawable-ldpi/ic_launcher.png:  PNG image data, 36 x 36, 8-bit/color RGBA, non-interlaced
