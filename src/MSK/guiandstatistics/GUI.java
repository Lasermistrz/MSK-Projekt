package MSK.guiandstatistics;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class GUI{

    private JFrame frame;
    private JPanel mainPanel;

    private JLabel textTimeSimulation;
    private double timeSimulation;
    private JLabel textEndTimeSimulation;
    private double endTimeSimulation;
    private JLabel textPacjentInRejestraction;
    private double pacjentInRejestraction;
    private JLabel textPacjentInWaitingRoom;
    private double pacjentInWaitingRoom;
    private JList listDoctors;
    private List<Doctor> doctors;
    private JList listConsultingRoom;
    private List<ConsultingRoom> consultingRooms;

    public GUI(){
        frame = new JFrame();
        frame.setTitle("MSK");
        mainPanel = new JPanel();

        JPanel upPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel downPanel = new JPanel();

        timeSimulation = 0;
        endTimeSimulation = 0;
        pacjentInRejestraction = 0;
        pacjentInWaitingRoom = 0;

        doctors = new ArrayList<Doctor>();
        consultingRooms = new ArrayList<ConsultingRoom>();


        //up panel
        JLabel labelTimeSimulation = new JLabel("Aktualny czas symulacji:");
        textTimeSimulation = new JLabel("0");

        JLabel labelEndTimeSimulation = new JLabel("Czas zakończenia symulacji:");
        textEndTimeSimulation = new JLabel("0");

        //center panel
        JLabel labelDoctors = new JLabel("Dostępni lekarze:");
        listDoctors = new JList(new DefaultListModel<>());

        JLabel labelConsultingRoom = new JLabel("Dostępne gabinety:");
        listConsultingRoom = new JList(new DefaultListModel<>());

        //downPanel
        JLabel labelPacjentInRejestraction = new JLabel("Ilość pacjentów w rejestracji:");
        textPacjentInRejestraction = new JLabel("0");

        JLabel labelPacjentInWaitingRoom = new JLabel("Ilość pacjentów w poczekalni:");
        textPacjentInWaitingRoom = new JLabel("0");


        upPanel.setLayout(new GridLayout(2, 4, 10, 10));
        upPanel.setBounds(10, 5, 800, 80);
        upPanel.add(labelTimeSimulation);
        upPanel.add(textTimeSimulation);
        upPanel.add(labelEndTimeSimulation);
        upPanel.add(textEndTimeSimulation);
        upPanel.add(labelDoctors);
        upPanel.add(new JLabel(""));
        upPanel.add(labelConsultingRoom);
        upPanel.add(new JLabel(""));


        centerPanel.setLayout(new GridLayout(1, 2, 10, 10));
        centerPanel.setBounds(10, 85, 800, 250);
        centerPanel.add(listDoctors);
        centerPanel.add(listConsultingRoom);


        downPanel.setLayout(new GridLayout(1, 4, 10, 10));
        downPanel.setBounds(10, 345, 800, 80);
        downPanel.add(labelPacjentInRejestraction);
        downPanel.add(textPacjentInRejestraction);
        downPanel.add(labelPacjentInWaitingRoom);
        downPanel.add(textPacjentInWaitingRoom);


        mainPanel.add(upPanel);
        mainPanel.add(centerPanel);
        mainPanel.add(downPanel);
        mainPanel.setLayout(null);


        frame.add(mainPanel);
        frame.setSize(830, 470);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setVisible(true);


        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });

    }

    public void SetTimeSimulation(double time){
        textTimeSimulation.setText(String.format("%.2f", time));
    }

    public void SetEndTimeSimulation(double time){
        textEndTimeSimulation.setText(String.format("%.2f", time));
    }

    public void SetPacjentInRejestraction(int time){
        textPacjentInRejestraction.setText(String.valueOf(time));
    }

    public void SetPacjentInWaitingRoom(int time){
        textPacjentInWaitingRoom.setText(String.valueOf(time));
    }

    public void AddNewDoctor(int id, boolean working){
        Doctor doc = new Doctor(id, working);
        doctors.add(doc);
        UpdateDoctors();
    }

    public void AddConsultingRoom(int id, boolean inUse){
        ConsultingRoom room = new ConsultingRoom(id, inUse);
        consultingRooms.add(room);
        UpdateConsultingRooms();
    }

    public void DeleteDoctor(int id){
        boolean pom = doctors.removeIf(n -> n.id == id);
        if(pom)
            UpdateDoctors();
    }

    public void DeleteConsultingRoom(int id){
        boolean pom = consultingRooms.removeIf(n -> n.id == id);
        if(pom)
            UpdateConsultingRooms();
    }

    public void SetDoctorWorking(int id, boolean working){
        Doctor doc = doctors.stream().filter((n) -> n.id == id).findFirst().orElse(null);
        if(doc != null) {
            doc.working = working;
            UpdateDoctors();
        }
    }

    public void SetConsultingRoomInUse(int id, boolean working){
       ConsultingRoom room = consultingRooms.stream().filter((n) -> n.id == id).findFirst().orElse(null);
       if(room != null) {
           room.inUse = working;
           UpdateConsultingRooms();
       }
    }

    private void UpdateDoctors(){
        DefaultListModel model = new DefaultListModel();
        for(Doctor x : doctors) {
            if(x.working)
                model.addElement("Doctor " + x.id + " examines the patient");
            else
                model.addElement("Doctor " + x.id + " waiting for patient");
        }
        listDoctors.setModel(model);
    }

    private void UpdateConsultingRooms(){
        DefaultListModel model = new DefaultListModel();
        for(ConsultingRoom x : consultingRooms) {
            if(x.inUse)
                model.addElement("Consulting room " + x.id + " is busy");
            else
                model.addElement("Consulting room " + x.id + " is empty");
        }
        listDoctors.setModel(model);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new  GUI();
    }

}
