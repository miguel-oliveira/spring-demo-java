package miguel.oliveira.demo.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class MyMessageFormatterTest {

  private MyMessageFormatter myMessageFormatter;

  @BeforeEach
  void setUp() {
    this.myMessageFormatter = new MyMessageFormatter();
  }

  @ParameterizedTest
  @ArgumentsSource(MessageFormattingTestCases.class)
  void testFormat(String message, String expected, Object... parameters) {
    assertEquals(expected, myMessageFormatter.format(message, parameters));
  }

  private static class MessageFormattingTestCases implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          //@formatter:off
          Arguments.of("Hello {0}, {0abc}!", "Hello World, {0abc}!", new String[]{"World"}),
          Arguments.of("Hello {0}, {bc0}!", "Hello World, {bc0}!", new String[]{"World"}),
          Arguments.of("Hello {}!", "Hello World!", new String[]{"World"}),
          Arguments.of("Hello {0} \\{}!", "Hello World {}!", new String[]{"World"}),
          Arguments.of("Hello {0} {0} {1} \\{}!", "Hello World World Hello {}!", new String[]{"World", "Hello"}),
          Arguments.of("Hello {0} {0} \\{1} \\{}!", "Hello World World {1} {}!", new String[]{"World", "Hello"}),
          Arguments.of("Hello { {}!", "Hello { World!", new String[]{"World"}),
          Arguments.of("Hello {}! I'm {}", "Hello World! I'm Bob", new String[]{"World", "Bob"}),
          Arguments.of("Hello {0}! I'm {1}", "Hello World! I'm Bob", new String[]{"World", "Bob"}),
          Arguments.of("Hello {1}{}! I'm {0}", "Hello {1}World! I'm {0}", new String[]{"World", "Bob"}),
          Arguments.of("Hello} {}!", "Hello} World!", new String[]{"World"}),
          Arguments.of("Hello''' {}!", "Hello''' World!", new String[]{"World"}),
          Arguments.of("Hello''' {0}!", "Hello''' World!", new String[]{"World"}),
          Arguments.of("Hello''' '{0}'!", "Hello''' 'World'!", new String[]{"World"}),
          Arguments.of("Hello \\{ {0}!}{", "Hello { World!}{", new String[]{"World"}),
          Arguments.of("Hello {my stuff} {0}!", "Hello {my stuff} World!", new String[]{"World"}),
          Arguments.of("Hello {my stuff} {0}}}!", "Hello {my stuff} World}}!", new String[]{"World"}),
          Arguments.of("Hello {{my {stuff} \\{a{0}}}.}!", "Hello {{my {stuff} {aWorld}}.}!", new String[]{"World"})
          //@formatter:on
      );
    }
  }

}
