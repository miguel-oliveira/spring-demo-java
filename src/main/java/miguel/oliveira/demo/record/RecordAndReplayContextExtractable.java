package miguel.oliveira.demo.record;

import java.util.List;

public interface RecordAndReplayContextExtractable {

  List<Info> extractContext(RecordAndReplayContextConverter service);

  void injectContext(List<Info> info, RecordAndReplayContextConverter service);

}
