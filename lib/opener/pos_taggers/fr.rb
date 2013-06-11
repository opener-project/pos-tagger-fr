require_relative 'fr/version'

module Opener
  module POSTaggers
    class FR
      attr_reader :kernel, :lib

      def command(opts=[])
        "java -jar #{kernel} -l fr #{opts.join(' ')}"
      end
      
      def run(opts=ARGV)
        `#{command(opts)}`
      end
      
      protected
      
      def core_dir
        File.expand_path("../../../core", File.dirname(__FILE__))
      end
      
      def kernel
        core_dir+'/target/Vicom-postagger_FR-0.9.jar'
      end      
      
    end
  end
end



