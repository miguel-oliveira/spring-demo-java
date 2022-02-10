package miguel.oliveira.demo.record;

import java.util.List;

public interface RecordAndPlaybackContextExtractable {

  List<Info> extractContext(RecordAndPlaybackContextConverter service);

  void injectContext(List<Info> info, RecordAndPlaybackContextConverter service);

}
