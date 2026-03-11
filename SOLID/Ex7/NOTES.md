# Ex7 — My Notes (ISP Fix: Smart Classroom Devices)

---

## What the problem asks to do

A smart classroom has 4 devices:
- **Projector** — can be turned on/off and you can connect an input (HDMI)
- **LightsPanel** — can be turned on/off and brightness can be set
- **AirConditioner** — can be turned on/off and temperature can be set
- **AttendanceScanner** — just scans attendance (no power button, no brightness, nothing else)

A `ClassroomController` operates all of them.

The problem says: **there's one giant interface `SmartClassroomDevice` with ALL methods** — power, brightness, temperature, scan, input connect. Every device implements all of them even if it doesn't need most. Fix it.

---

## The concept being used: ISP (Interface Segregation Principle)

Imagine a TV remote that also has a washing machine control, a microwave timer, and a fan speed button — even though you just want to watch TV.

Every time you pick up the remote, you're confused by 30 buttons you'll never use. If you press the wrong one by accident — things break.

**ISP says:** Don't force a class to implement methods it doesn't need. Split the big interface into small, focused ones.

---

## What was broken (Before the fix)

`SmartClassroomDevice` had 5 methods: `powerOn`, `powerOff`, `setBrightness`, `setTemperatureC`, `scanAttendance`, `connectInput`.

Every device implemented ALL of them:
- `AttendanceScanner` had `powerOn()`, `setBrightness()`, `setTemperatureC()`, `connectInput()` — all doing nothing (dummy)
- `AirConditioner` had `setBrightness()`, `scanAttendance()`, `connectInput()` — dummy
- `LightsPanel` had `setTemperatureC()`, `scanAttendance()`, `connectInput()` — dummy

Dummy implementations are dangerous. They look like they work but do nothing. If the controller calls `scanner.powerOff()`, it silently does nothing — no error, no indication.

---

## Steps to identify and understand what to do

**Step 1 — Group the methods by what they describe**

- `powerOn`, `powerOff` → things that can be turned on/off → `PowerControl`
- `setBrightness` → things that control brightness → `BrightnessControl`
- `setTemperatureC` → things that control temperature → `TemperatureControl`
- `scanAttendance` → things that scan → `AttendanceScan`
- `connectInput` → things that connect to an input source → `InputConnect`

**Step 2 — Create 5 small interfaces**

Each interface has only the methods that logically belong together.

**Step 3 — Each device implements only what it actually does**

- `Projector` implements `PowerControl, InputConnect` (can turn on/off, can connect HDMI)
- `LightsPanel` implements `PowerControl, BrightnessControl`
- `AirConditioner` implements `PowerControl, TemperatureControl`
- `AttendanceScanner` implements only `AttendanceScan` (no power button, nothing else)

No dummies anywhere.

**Step 4 — Update ClassroomController and DeviceRegistry**

`DeviceRegistry` now stores `Object` and finds devices by capability interface using generics (`getFirst(AttendanceScan.class)`).

`ClassroomController` asks the registry: "give me the device that can scan", "give me the device that can control brightness" — it doesn't care what the device is, only what it can do.

---

## UML Diagram

```
   PowerControl        BrightnessControl     TemperatureControl
   (interface)          (interface)            (interface)
   powerOn()           setBrightness()        setTemperatureC()
   powerOff()
       ^                    ^                      ^
       |                    |                      |
   Projector            LightsPanel            AirConditioner
   + InputConnect       + PowerControl         + PowerControl

   AttendanceScan       InputConnect
   (interface)          (interface)
   scanAttendance()     connectInput()
       ^                    ^
       |                    |
   AttendanceScanner    Projector

                  +----------------------+
                  |    DeviceRegistry    |
                  |----------------------|
                  | - devices: List<Object>|
                  | + getFirst(Class<T>)  | ← finds by interface
                  +----------------------+
                           |
                  used by  v
                  +----------------------+
                  | ClassroomController  |
                  |----------------------|
                  | + startClass()       | ← asks for InputConnect, BrightnessControl...
                  | + endClass()         | ← asks for PowerControl devices
                  +----------------------+
```

---

## The story in one paragraph

All 4 classroom devices were forced to implement one fat interface with 6 methods — even `AttendanceScanner` had dummy `powerOn()` and `setBrightness()` that did nothing. We broke the fat interface into 5 small ones: `PowerControl`, `BrightnessControl`, `TemperatureControl`, `AttendanceScan`, `InputConnect`. Each device now implements only the interfaces that make sense for it. `AttendanceScanner` only implements `AttendanceScan` — no dummies. `ClassroomController` now asks the registry "give me something that can scan" or "give me something with brightness control" — it doesn't know or care what specific device it gets. That's ISP: no class is forced to implement what it doesn't need.
