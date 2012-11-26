Feature: Using files as input and output
  In order to reverse text
  Using a file as an input
  Using a file as an output

  Scenario Outline: Reverse the text
    Given the fixture file "<input_file>"
    And I put it through the kernel
    Then the output should match the fixture "<output_file>"
  Examples:
    | input_file         | output_file         |
    | sample_in.txt      | sample_out.txt      |
