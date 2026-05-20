package interfaces;

import enums.ResourceType;
import java.util.Map;

public interface Producible {
    Map<ResourceType, Integer> produce();
    int getOutput(ResourceType type);
}
