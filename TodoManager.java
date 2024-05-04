import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class TodoManager {

    static File[] allLists; // this doesn't change unless new file is created
    static Task[] selectedTask; // this changes frequently depending upon which list is selected
    //selectedTask stores the task title in an array
    static String selectedListName; // this stores the name of the currently selected list

    static class Task{
        int taskNum;
        String taskName;
        int priority;
        //date
        Task(){
            this.taskName = "";
            this.taskNum = 0;
            this.priority = 0;
        }
        Task(int taskNum, String taskName, int priority){
            this.taskName = taskName;
            this.taskNum = taskNum;
            this.priority = priority;
        }
    }

    // to set the lists names into an array of files
    public TodoManager(){
        updateAllListsArray();
    }
    public static void updateAllListsArray(){
        String folderPath = "TodoManager/Lists";
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if(files == null){
            return;
        }
        allLists = new File[files.length];
        int i = 0;
        for(File file : files){
            if(file.isFile() && file.getName().endsWith(".txt")){
                allLists[i++] = file;
            }
        }
    }

    public static void main(String[] args) {
        new TodoManager();
        while(true){
            //displayLists();
            boolean ch = setSelectedTask();
            if(!ch){
                return;
            }
            getTaskInput();
            System.out.println(selectedListName);
            displaySelectedTask();
            //saveListToFile();
        }
    }


    // display all the file names that hold lists
    static void displayLists(){
        int i = 1;
        for(File file : allLists){
            String fileName = file.getName();
            String name = fileName.substring(0, fileName.lastIndexOf('.'));
            System.out.println(i++ + " : "+name);
        }
    }

    // set the file you want to open into the selectedTask array
    static boolean setSelectedTask(){
        Scanner sc = new Scanner(System.in);

        while(true){
            System.out.println("\nSelect List to Open : ");
            System.out.println("0 : EXIT");
            displayLists();
            System.out.println(allLists.length+1 + " : MODIFY LISTS");
            System.out.print(">> ");
            int ch = sc.nextInt();
            if(ch == 0){
                return false;
            }
            if(ch == allLists.length+1){
                modifyLists();
                continue;
            }
            File file = allLists[ch-1];
            selectedListName = file.getName();
            int i = 1;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                while (br.readLine() != null) {
                    i++;
                }
            } catch (IOException e) {
                System.err.println("Error reading the file: " + e.getMessage());
            }
            // set size of selected Task
            selectedTask = new Task[i-1];
            setTaskToArray();
            break;
        }
        return true;
    }

    public static void modifyLists(){
        while(true){
            System.out.println("0 : Back     1 : Add New List     2 : Delete List     3 : Rename List ");
            System.out.print(">> ");
            Scanner sc = new Scanner(System.in);
            int ch = sc.nextInt();

            switch(ch){
                case 0 :
                    return;
                case 1 :
                    addList();
                    break;
                case 2 :
                    deleteList();
                    break;
                case 3 :
                    renameList();
                    break;
            }
        }
    }

    public static void addList(){
        // remember to update allLists[]
        String directoryPath = "TodoManager/Lists";
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter new List Name : ");
        String newFileName = sc.nextLine();
        String fileName = newFileName+ ".txt";
        for(File f : allLists){
            if((fileName.compareTo(f.getName())) == 0){
                System.out.println("List already Exists!");
                return;
            }
        }
        try {
            Path directory = Paths.get(directoryPath);
            Path filePath = Paths.get(directory.toString(), fileName);
            Files.createFile(filePath);

            System.out.println("(C)");
        } catch (IOException e) {
            System.out.println("ERROR occurred");
            return;
        }
        // code to update the allLists
        updateAllListsArray();
    }

    public static void deleteList(){
        System.out.println("0 : BACK");
        displayLists();
        Scanner sc = new Scanner(System.in);
        System.out.println("Select List to Delete : ");
        System.out.print(">> ");
        int ch = sc.nextInt();

        // double checking
        System.out.println("WARNING : ALL THE TASKS IN THE LIST WILL BE DELETED !!!");
        System.out.println("To confirm type \"confirm\" below : ");
        System.out.print(">> ");
        String confirm = sc.next();
        if(confirm.compareTo("confirm")!= 0){
            System.out.println("DELETION TERMINATED");
            ch = 0;
        }

        if(ch == 0){return;}

        String str = allLists[ch-1].getName();

        String filePath = "TodoManager/Lists/"+str;

        // Create a File object representing the file
        File file = new File(filePath);
        // Check if the file exists
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("(D)");
            } else {
                System.out.println("Deletion failed");
            }
        } else {
            System.out.println("file does not exist // should not occur");
        }
        updateAllListsArray();
    }

    public static void renameList(){
        System.out.println("0 : BACK");
        displayLists();
        Scanner sc = new Scanner(System.in);
        System.out.println("Select List to Rename : ");
        System.out.print(">> ");
        int ch = sc.nextInt();
        if(ch == 0){return;}

        String str = allLists[ch-1].getName();

        System.out.print("Enter New Name : ");
        sc.nextLine();
        String newName = sc.nextLine();

        String currentFilePath = "TodoManager/Lists/"+str;

        // Specify the new file path
        String newFilePath = "TodoManager/Lists/"+newName+".txt";

        // Create File objects for both the current and new files
        File currentFile = new File(currentFilePath);
        File newFile = new File(newFilePath);

        boolean success = currentFile.renameTo(newFile);
        if(!success){
            System.out.println("Rename Failed :(");
        }
        updateAllListsArray();
    }

    public static void setTaskToArray() {
        String listPath = "TodoManager/Lists/" + selectedListName;
        try (BufferedReader reader = new BufferedReader(new FileReader(listPath))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\$");
                if (parts.length == 3) {
                    selectedTask[index] = new Task(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]));
                    index++;
                }
            }
            System.out.println("(R)");
        } catch (IOException e) {
            System.out.println("An error occurred while reading tasks from file: " + e.getMessage());
        }
    }

    // display the selected task array whenever needed
    static void displaySelectedTask(){
        for(Task t : selectedTask){
            System.out.println(t.taskNum +" : "+t.taskName+ " : " + t.priority);
        }
    }

    //save the current instance of selected list to the txt file
    static void saveListToFile(){
        // save data from selectedTask[] to txt file named selectedListName
        String listPath = "TodoManager/Lists/" + selectedListName;
        try (FileWriter writer = new FileWriter(listPath)) {
            for (Task task : selectedTask) {
                writer.write(task.taskNum + "$" + task.taskName + "$" + task.priority + "\n");
            }
            System.out.println("(W)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing tasks to file: " + e.getMessage());
        }
    }




    // all methods related to particular lists i.e. for tasks in a list
    // add task, delete task, modify task,

    static void getTaskInput(){
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("--- " + selectedListName.substring(0,selectedListName.length()-4) + " ---");
            displaySelectedTask();
            System.out.println();
            System.out.println("0 : back     1 : New Task    2 : Delete Task     3 : Modify Task");
            System.out.print(">> ");
            int ch = sc.nextInt();
            switch (ch){
                case 0 :
                    return;
                case 1 :
                    addTask();
                    break;
                case 2 :
                    displaySelectedTask();
                    System.out.println("Select Task to Delete : ");
                    System.out.print(">> ");
                    int del = sc.nextInt();
                    deleteTask(del);
                    break;
                case 3 :
                    displaySelectedTask();
                    System.out.println("Select Task to Modify : ");
                    System.out.print(">> ");
                    int mod = sc.nextInt();
                    modifyTask(mod);
            }
        }
    }

    static void addTask(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Task :");
        String name = sc.nextLine();
        System.out.print("Enter Priority : ");
        int pr = sc.nextInt();
        //int arrLen = selectedTask.length;
        //Task t1 = new Task(arrLen, name, pr);

        Task[] temp = new Task[selectedTask.length +1];
        for(int i = 0; i<selectedTask.length; i++){
            temp[i] = new Task();
            temp[i].taskNum = selectedTask[i].taskNum;
            temp[i].taskName = selectedTask[i].taskName;
            temp[i].priority = selectedTask[i].priority;
        }
        temp[selectedTask.length] = new Task(temp.length, name, pr);
        selectedTask = temp;
        saveListToFile();
    }

    static void deleteTask(int del){
        if(del > selectedTask.length){
            System.out.println("Select a valid Task!!!");
            return;
        }
        int arrLen = selectedTask.length;
        Task[] temp = new Task[arrLen -1];
        for(int i = del; i< selectedTask.length; i++){
            selectedTask[i-1] = selectedTask[i];
        }
        for(int i = 0; i< temp.length; i++){
            temp[i] = selectedTask[i];
            temp[i].taskNum = i+1;
        }
        selectedTask = temp;
        saveListToFile();
    }

    static void modifyTask(int mod){
        Scanner sc = new Scanner(System.in);
        System.out.println("Element to Modify : ");
        System.out.println("1 : Task Name ");
        System.out.println("2 : Priority");
        System.out.print(">> ");
        int ch = Integer.parseInt(sc.nextLine());
        switch(ch){
            case 1:
                System.out.println("New Title : ");
                System.out.print(">> ");
                selectedTask[mod -1].taskName = sc.nextLine();
                break;
            case 2 :
                System.out.println("Set Priority : ");
                System.out.print(">> ");
                selectedTask[mod -1].priority = sc.nextInt();
        }
        saveListToFile();
    }
}
