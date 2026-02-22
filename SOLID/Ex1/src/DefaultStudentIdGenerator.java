public class DefaultStudentIdGenerator implements StudentIdGenerator {
    @Override
    public String nextId(int currentCount) {
        return IdUtil.nextStudentId(currentCount);
    }
}
