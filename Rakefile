require "bundler/gem_tasks"

task :default => :install do
  puts "Tasks completed"
end

task :maven do
 puts "Changing to core dir"
 Dir.chdir("core") do
 puts "Executing maven tasks"
  sh "mvn clean package"
    end
end

task :build => :maven do

end

task :install => :build do

end
