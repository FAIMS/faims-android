#!/usr/bin/env ruby

def scaleImage(image)
  system("convert #{image} -filter spline -resize 96x -background transparent -gravity center -extent 96x96 -unsharp 0x1 ./faimsandroidapp/src/main/res/drawable-xhdpi/ic_launcher.png")
  system("convert #{image} -filter spline -resize 96x -background transparent -gravity center -extent 96x96 -unsharp 0x1 ./faimsandroidapp/src/main/res/drawable/ic_launcher.png")
  system("convert #{image} -filter spline -resize 72x -background transparent -gravity center -extent 72x72 -unsharp 0x1 ./faimsandroidapp/src/main/res/drawable-hdpi/ic_launcher.png")
  system("convert #{image} -filter spline -resize 48x -background transparent -gravity center -extent 48x48 -unsharp 0x1 ./faimsandroidapp/src/main/res/drawable-mdpi/ic_launcher.png")
  system("convert #{image} -filter spline -resize 36x -background transparent -gravity center -extent 36x36 -unsharp 0x1 ./faimsandroidapp/src/main/res/drawable-ldpi/ic_launcher.png")
end

puts "Removing module specific configuration"
system("rm module.properties")
puts "Restoring FAIMS icon"
scaleImage("faims_logo.png")
puts "Cleaning project"
system("./gradlew clean > vanilla.log 2>&1")
puts "Building project"
system("./gradlew build >> vanilla.log 2>&1")
puts "Copying APK"
system("./gradlew publishAll >> vanilla.log 2>&1")
puts "Done"
