require File.expand_path('../lib/opener/pos_taggers/fr/version', __FILE__)

Gem::Specification.new do |gem|
  gem.name          = "opener-pos-tagger-fr"
  gem.version       = Opener::POSTaggers::FR::VERSION
  gem.authors       = ["development@olery.com"]
  gem.summary       = "POS tagging for French"
  gem.description   = gem.summary
  gem.homepage      = "http://opener-project.github.com/"
  gem.has_rdoc      = "yard"
  gem.required_ruby_version = ">= 1.9.2"

  gem.files = Dir.glob([
    'core/target/Vicom-postagger_FR-*.jar',
    'lib/**/*',
    '*.gemspec',
    'README.md'
  ]).select { |file| File.file?(file) }

  gem.executables = Dir.glob('bin/*').map { |file| File.basename(file) }

  gem.add_dependency 'opener-build-tools'

  gem.add_development_dependency 'rspec', '~> 3.0'
  gem.add_development_dependency 'cucumber'
  gem.add_development_dependency 'rake'
end
