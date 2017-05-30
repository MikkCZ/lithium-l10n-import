package cz.mikk.mozilla.sumo.importer.structures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(staticName = "of")
public class Pair<F, S> {
    @NonNull
    private final F first;
    @NonNull
    private final S second;
}
