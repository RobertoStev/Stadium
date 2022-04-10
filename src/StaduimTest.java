import java.util.*;

class SeatTakenException extends Exception {
    public SeatTakenException() {
        super();
    }
}

class SeatNotAllowedException extends Exception {
    public SeatNotAllowedException() {
        super();
    }
}

class Seat implements Comparable<Seat> {
    int number;
    int type;

    public Seat(int number, int type) {
        this.number = number;
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public int getType() {
        return type;
    }

    @Override
    public int compareTo(Seat o) {
        return Integer.compare(this.number, o.number);
    }
}

class Sector {
    String code;
    int seats;
    int freeSeats; //broj na slobodni mesta
    Set<Seat> allSeats;

    public Sector(String code, int seats) {
        this.code = code;
        this.seats = seats;
        this.freeSeats = seats;
        allSeats = new TreeSet<>();
    }

    public String getCode() {
        return code;
    }

    public int getSeats() {
        return seats;
    }

    public int getFreeSeats() {
        return freeSeats;
    }

    @Override
    public String toString() {
        int percentFreeSeats = seats - freeSeats;
        return String.format("%s\t%d/%d\t%.1f%%", code, freeSeats, seats, percentFreeSeats / (seats / 100.0));
    }
}

class Stadium {
    String name;
    Map<String, Sector> sectors; //key - code na sector, value - objekt od klasata Sector
    Set<String> allSectors;

    public Stadium(String name) {
        this.name = name;
        sectors = new TreeMap<>();
        allSectors = new HashSet<>();
    }

    public void createSectors(String[] sectorNames, int[] sizes) {
        for (int i = 0; i < sectorNames.length; i++) { //nizite se so ista golemina
            if (!allSectors.contains(sectorNames[i])) {
                Sector sector = new Sector(sectorNames[i], sizes[i]);
                this.sectors.put(sectorNames[i], sector);
            }
        }
    }

    public void buyTicket(String sectorName, int seat, int type) throws SeatTakenException, SeatNotAllowedException {
        Sector sector = sectors.get(sectorName);
        if (sector.allSeats.isEmpty()) { //ako nema zafateno nitu edno sedishte
            Seat takeSeat = new Seat(seat, type);
            sector.allSeats.add(takeSeat);
            sector.freeSeats--; //namali go brojot na slobodni mesta
            return;
        }

        for (Seat s : sector.allSeats) { //proveri dali sedishteto e zafateno
            if (s.number == seat) {
                throw new SeatTakenException();
            } else {
                if (type == 0 || s.type == type) { //proveri go type
                    Seat takeSeat = new Seat(seat, type);
                    sector.allSeats.add(takeSeat);
                    sector.freeSeats--;
                    return;
                } else {
                    throw new SeatNotAllowedException();
                }
            }
        }

    }

    public void showSectors() {
        sectors.values().stream()
                .sorted(Comparator.comparing(Sector::getFreeSeats).reversed().thenComparing(Sector::getCode))
                .forEach(sector -> System.out.println(sector));
    }
}

public class StaduimTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}
