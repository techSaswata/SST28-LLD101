import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private final int size;
    private final int totalCells;
    private final Map<Integer, BoardEntity> entityMap;

    public Board(int size, List<BoardEntity> entities) {
        this.size = size;
        this.totalCells = size * size;
        this.entityMap = new HashMap<>();
        for (BoardEntity e : entities) {
            entityMap.put(e.getStart(), e);
        }
    }

    public int getTotalCells() { return totalCells; }
    public int getSize() { return size; }

    public int getNextPosition(int position) {
        BoardEntity entity = entityMap.get(position);
        if (entity != null) {
            System.out.println("  Hit " + entity);
            return entity.getEnd();
        }
        return position;
    }

    public Map<Integer, BoardEntity> getEntityMap() { return entityMap; }
}
