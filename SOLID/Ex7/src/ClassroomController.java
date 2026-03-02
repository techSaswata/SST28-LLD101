public class ClassroomController {
    private final DeviceRegistry reg;
    private PowerControl projectorPower;
    private PowerControl lightsPower;
    private PowerControl acPower;

    public ClassroomController(DeviceRegistry reg) { this.reg = reg; }

    public void startClass() {
        InputConnect pj = reg.getFirst(InputConnect.class);
        if (pj instanceof PowerControl) {
            projectorPower = (PowerControl) pj;
            projectorPower.powerOn();
        }
        pj.connectInput("HDMI-1");

        BrightnessControl lights = reg.getFirst(BrightnessControl.class);
        if (lights instanceof PowerControl) lightsPower = (PowerControl) lights;
        lights.setBrightness(60);

        TemperatureControl ac = reg.getFirst(TemperatureControl.class);
        if (ac instanceof PowerControl) acPower = (PowerControl) ac;
        ac.setTemperatureC(24);

        AttendanceScan scan = reg.getFirst(AttendanceScan.class);
        System.out.println("Attendance scanned: present=" + scan.scanAttendance());
    }

    public void endClass() {
        System.out.println("Shutdown sequence:");
        if (projectorPower != null) projectorPower.powerOff();
        if (lightsPower != null) lightsPower.powerOff();
        if (acPower != null) acPower.powerOff();
    }
}
