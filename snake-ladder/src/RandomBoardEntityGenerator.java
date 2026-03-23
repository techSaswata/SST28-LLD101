import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomBoardEntityGenerator implements BoardEntityGenerator {
    private final int snakeCount;
    private final int ladderCount;
    private final Random random = new Random();

    public RandomBoardEntityGenerator(int snakeCount, int ladderCount) {
        this.snakeCount = snakeCount;
        this.ladderCount = ladderCount;
    }

    @Override
    public List<BoardEntity> generate(int boardSize) {
        int totalCells = boardSize * boardSize;
        List<BoardEntity> entities = new ArrayList<>();
        Set<Integer> usedCells = new HashSet<>();
        usedCells.add(1);
        usedCells.add(totalCells);

        for (int i = 0; i < snakeCount; i++) {
            int start = pickFree(usedCells, 2, totalCells - 1);
            if (start == -1) break;
            int end = random.nextInt(start - 1) + 1;
            entities.add(new BoardEntity(start, end, "SNAKE"));
        }

        for (int i = 0; i < ladderCount; i++) {
            int start = pickFree(usedCells, 2, totalCells - 1);
            if (start == -1) break;
            int end = start + random.nextInt(totalCells - start) + 1;
            if (end > totalCells) end = totalCells;
            entities.add(new BoardEntity(start, end, "LADDER"));
        }

        return entities;
    }

    private int pickFree(Set<Integer> used, int min, int max) {
        for (int attempt = 0; attempt < 100; attempt++) {
            int cell = random.nextInt(max - min + 1) + min;
            if (!used.contains(cell)) {
                used.add(cell);
                return cell;
            }
        }
        return -1;
    }
}
