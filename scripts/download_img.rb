require 'open-uri'

dst_dir = File.expand_path("../../app/src/main/res/drawable-nodpi", __FILE__)
raise "NoDirectory" unless Dir.exist?(dst_dir)

(1..55).each do |n|
  n = "%02d" % n
  url = "http://www.21j.jp/fu53/image/#{n}.jpg"
  file_name = "tokaido_#{n}.jpg"
  file_path = File.join(dst_dir, file_name)
  puts "%s > %s" % [url, file_name]

  open(url) do |src|
    open(file_path, "w") do |dst|
      dst.write src.read
    end
  end
end
