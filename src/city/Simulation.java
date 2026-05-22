package city;

import cell.Cell;
import cell.utility.UtilityProvider;
import cell.zone.Commercial;
import cell.zone.Housing;
import cell.zone.Industrial;
import cell.zone.Zone;
import enums.ResourceType;
import enums.ServiceType;
import enums.UtilityType;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.Map;
import java.util.Queue;

public class Simulation {
    private final City city;
    private final PrintStream out;
    private final EnumMap<ResourceType, Integer> resourcePools;

    public Simulation(City city, PrintStream out) {
        this.city = city;
        this.out = out;
        this.resourcePools = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            resourcePools.put(type, 0);
        }
    }

    public void run(int ticks) {
        for (int tick = 1; tick <= ticks; tick++) {
            out.println("Tick " + tick);
            runTick();
        }
    }

    private void runTick() {
        for (Zone zone : city.getZones()) {
            zone.resetTickData();
        }

        distributeServices();
        distributeUtilities();
        distributeResources();
        updateZones();
        accumulateProduction();
    }

    private void distributeServices() {
        city.getServiceProviders().forEach(provider ->
                city.getZonesInRadius(provider.getRow(), provider.getCol(), provider.getRadius())
                        .forEach(zone -> {
                            if (usesService(zone, provider.getServiceType())) {
                                zone.receiveService(provider.getServiceType());
                                out.println(zoneName(zone) + " at (" + zone.getRow() + "," + zone.getCol()
                                        + ") received " + provider.getServiceType().getLabel() + " service");
                            }
                        })
        );
    }

    private boolean usesService(Zone zone, ServiceType serviceType) {
        if (serviceType == ServiceType.SECURITY) {
            return true;
        }
        return zone instanceof Housing;
    }

    private void distributeUtilities() {
        for (UtilityProvider provider : city.getUtilityProviders()) {
            distributeUtility(provider);
        }
    }

    private void distributeUtility(UtilityProvider provider) {
        UtilityType utilityType = provider.getUtilityType();
        int remaining = provider.getCapacity();
        boolean[][] visited = new boolean[city.getRowCount()][city.getColumnCount()];
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{provider.getRow(), provider.getCol()});
        visited[provider.getRow()][provider.getCol()] = true;

        while (!queue.isEmpty() && remaining > 0) {
            int[] position = queue.remove();
            Cell cell = city.getCell(position[0], position[1]);

            if (cell instanceof Zone zone && zone.getRequiredUtilities().contains(utilityType)) {
                int need = Math.max(0, zone.getDemand() - zone.getUtilityAmount(utilityType));
                int amount = Math.min(need, remaining);
                if (amount > 0) {
                    zone.receiveUtility(utilityType, amount);
                    remaining -= amount;
                    out.println(zoneName(zone) + " at (" + zone.getRow() + "," + zone.getCol()
                            + ") received " + amount + " " + utilityType.getLabel());
                }
            }

            if (remaining <= 0) {
                break;
            }

            for (int[] neighbor : city.getNeighbors(position[0], position[1])) {
                if (!visited[neighbor[0]][neighbor[1]]) {
                    Cell next = city.getCell(neighbor[0], neighbor[1]);
                    if (next != null && next.isConnectable()) {
                        visited[neighbor[0]][neighbor[1]] = true;
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    private void distributeResources() {
        int populationConsumers = countConsumers(ResourceType.POPULATION);
        int goodsConsumers = countConsumers(ResourceType.GOODS);
        int lifestyleConsumers = countConsumers(ResourceType.LIFESTYLE);

        int populationShare = resourceShare(ResourceType.POPULATION, populationConsumers);
        int goodsShare = resourceShare(ResourceType.GOODS, goodsConsumers);
        int lifestyleShare = resourceShare(ResourceType.LIFESTYLE, lifestyleConsumers);

        for (Zone zone : city.getZones()) {
            if (zone.consumesResource(ResourceType.POPULATION) && populationShare > 0) {
                zone.receiveResource(ResourceType.POPULATION, populationShare);
                logResource(zone, ResourceType.POPULATION, populationShare);
            }
            if (zone.consumesResource(ResourceType.GOODS) && goodsShare > 0) {
                zone.receiveResource(ResourceType.GOODS, goodsShare);
                logResource(zone, ResourceType.GOODS, goodsShare);
            }
            if (zone.consumesResource(ResourceType.LIFESTYLE) && lifestyleShare > 0) {
                zone.receiveResource(ResourceType.LIFESTYLE, lifestyleShare);
                logResource(zone, ResourceType.LIFESTYLE, lifestyleShare);
            }
        }

        for (ResourceType type : ResourceType.values()) {
            resourcePools.put(type, 0);
        }
    }

    private int resourceShare(ResourceType type, int consumers) {
        if (consumers == 0) {
            return 0;
        }
        return resourcePools.get(type) / consumers;
    }

    private int countConsumers(ResourceType type) {
        int count = 0;
        for (Zone zone : city.getZones()) {
            if (zone.consumesResource(type)) {
                count++;
            }
        }
        return count;
    }

    private void logResource(Zone zone, ResourceType type, int amount) {
        out.println(zoneName(zone) + " at (" + zone.getRow() + "," + zone.getCol()
                + ") received " + amount + " " + type.getLabel());
    }

    private void updateZones() {
        for (Zone zone : city.getZones()) {
            int oldLevel = zone.getLevel();
            zone.update();
            ResourceType producedType = producedType(zone);
            int amount = zone.getOutput(producedType);
            out.println(zoneName(zone) + " at (" + zone.getRow() + "," + zone.getCol()
                    + ") generated " + amount + " " + producedType.getLabel());
            int newLevel = zone.getLevel();
            if (newLevel > oldLevel) {
                out.println(zoneName(zone) + " at (" + zone.getRow() + "," + zone.getCol()
                        + ") levels up from " + oldLevel + " to " + newLevel);
            } else if (newLevel < oldLevel) {
                out.println(zoneName(zone) + " at (" + zone.getRow() + "," + zone.getCol()
                        + ") levels down from " + oldLevel + " to " + newLevel);
            }
        }
    }

    private void accumulateProduction() {
        for (Zone zone : city.getZones()) {
            for (Map.Entry<ResourceType, Integer> entry : zone.getLastOutput().entrySet()) {
                resourcePools.put(entry.getKey(), resourcePools.get(entry.getKey()) + entry.getValue());
            }
        }
    }

    private ResourceType producedType(Zone zone) {
        if (zone instanceof Housing) {
            return ResourceType.POPULATION;
        }
        if (zone instanceof Industrial) {
            return ResourceType.GOODS;
        }
        if (zone instanceof Commercial) {
            return ResourceType.LIFESTYLE;
        }
        throw new IllegalStateException("Unknown zone type: " + zone.getClass().getName());
    }

    private String zoneName(Zone zone) {
        if (zone instanceof Housing) {
            return "House";
        }
        if (zone instanceof Industrial) {
            return "Industrial";
        }
        if (zone instanceof Commercial) {
            return "Commercial";
        }
        return zone.getClass().getSimpleName();
    }
}