require 'tempfile'

module Opener
  module POSTaggers
    class FR

      attr_reader :kernel, :lib

      def initialize
        core_dir    = File.expand_path("../core", File.dirname(__FILE__))

        @kernel      = core_dir+'/target/Vicom-postagger_FR-0.9.jar'
        @lib         = core_dir+'/lib/'
      end

      def command(opts=[])
        "java -jar #{kernel} -l #{lib} #{opts.join(' ')}"
      end
    end
  end
end


