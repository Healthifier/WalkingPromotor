require 'date'

user_ids = [
  "UserA",
  "UserB",
  "UserC",
]

start = Date.new(2015, 3, 1)
finish = Date.new(2015, 9, 1)

user_ids.each do |user_id|
  (start ... finish).each do |date|
    walked = rand(1500)
    puts [date.iso8601, walked, user_id].join("\t")
  end
end
