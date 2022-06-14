package MSK.Statystyka;

import java.util.ArrayList;
import java.util.List;

public class Statistics {

    private List<StatisticTime> enterToRegistration;
    private List<StatisticTime> enterToWaitingRoom;
    private List<StatisticTime> enterToDoctor;
    private List<StatisticTime> waitingForConsultingRoom;
    private List<StatisticTime> enterToConsultingRoom;
    private List<StatisticTime> leavesClinic;

    public Statistics(){
        enterToRegistration = new ArrayList<StatisticTime>();
        enterToWaitingRoom = new ArrayList<StatisticTime>();
        enterToDoctor = new ArrayList<StatisticTime>();
        waitingForConsultingRoom = new ArrayList<StatisticTime>();
        enterToConsultingRoom = new ArrayList<StatisticTime>();
        leavesClinic = new ArrayList<StatisticTime>();
    }

    public void EnterToRegistration(int id, double time){
        StatisticTime st = new StatisticTime(id,time);
        enterToRegistration.add(st);
    }

    public void EnterToWaitingRoom(int id, double time){
        StatisticTime st = new StatisticTime(id,time);
        enterToWaitingRoom.add(st);
    }

    public void EnterToDoctor(int id, double time){
        StatisticTime st = new StatisticTime(id,time);
        enterToDoctor.add(st);
    }

    public void EnterToConsultingRoom(int id, double time){
        StatisticTime st = new StatisticTime(id,time);
        enterToConsultingRoom.add(st);
    }

    public void WaitingForConsultingRoom(int id, double time){
        StatisticTime st = new StatisticTime(id,time);
        waitingForConsultingRoom.add(st);
    }
    public void LeavesClinic(int id, double time){
        StatisticTime st = new StatisticTime(id,time);
        leavesClinic.add(st);
    }

    public double GetAverageTimeInClinic(){
        double time = 0;
        int count = 0;
        for (StatisticTime x: leavesClinic) {
            StatisticTime st = enterToRegistration.stream().filter((n) -> n.PatientId == x.PatientId).findFirst().orElse(null);
            if(st != null) {
                time+= x.Time-st.Time;;
                count++;
            }
        }
        return time/count;
    }

    public double GetAverageTimeInWaitingRoom(){
        double time = 0;
        int count = 0;
        for (StatisticTime x: enterToDoctor) {
            StatisticTime st = enterToWaitingRoom.stream().filter((n) -> n.PatientId == x.PatientId).findFirst().orElse(null);
            if(st != null) {
                time+= x.Time-st.Time;;
                count++;
            }
        }
        return time/count;
    }

    public double GetAverageTimeInRegistration(){
        double time = 0;
        int count = 0;
        for (StatisticTime x: enterToWaitingRoom) {
            StatisticTime st = enterToRegistration.stream().filter((n) -> n.PatientId == x.PatientId).findFirst().orElse(null);
            if(st != null) {
                time+= x.Time-st.Time;;
                count++;
            }
        }
        return time/count;
    }

    public double GetAverageTimeInConsultingRoom(){
        double time = 0;
        int count = 0;
        for (StatisticTime x: enterToConsultingRoom) {
            StatisticTime st = leavesClinic.stream().filter((n) -> n.PatientId == x.PatientId).findFirst().orElse(null);
            if(st != null) {
                time+= st.Time-x.Time;
                count++;
            }
        }
        return time/count;
    }

    public double GetTimeLastPatient(){
        double time = 0;
        for (StatisticTime x: leavesClinic) {
            if(x.Time > time) {
                time = x.Time;
            }
        }
        return time;
    }
}
