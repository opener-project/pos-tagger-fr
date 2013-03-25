require 'tempfile'

module Opener
  module Kernel
    module Vicom
      module POSTagger
        module Lite
          class FR
            VERSION = "0.0.2"

            attr_reader :kernel, :lib

            def initialize
              core_dir    = File.expand_path("../core", File.dirname(__FILE__))

              @kernel      = core_dir+'/postagger_french.jar'
              @lib         = core_dir+'/lib/'
            end

            def command(opts={})
              arguments = opts[:arguments] || []
              arguments << "-n" if opts[:test]

              "cat #{opts[:input]} | java -jar #{kernel} #{lib} #{opts[:input]} #{arguments.join(' ')}"

            end

          end
        end
      end
    end
  end
end


