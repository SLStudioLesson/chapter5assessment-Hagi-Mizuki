package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;

    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * 
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    // タスクの情報を一覧表示する
    public void showAll(User loginUser) throws AppException {
        // findAllメソッドを実行して、データの一覧取得
        List<Task> tasks = taskDataAccess.findAll();
        // 取得したデータを表示する
        tasks.forEach(task -> {
            String status = "未着手";
            if (task.getStatus() == 1) {
                status = "着手中";
            } else if (task.getStatus() == 2) {
                status = "完了";
            }

            String repUser = task.getRepUser().getName();
            if (task.getRepUser().getCode() == loginUser.getCode()) {
                repUser = "あなた";
            }
            System.out.println("タスク名：" + repUser + "が担当しています" + ", ステータス: " + status);
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code        タスクコード
     * @param name        タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser   ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    // CSVに書き込む
    public void save(int code, String name, int repUserCode, User loginUser) throws AppException {

        // // 担当者コードを基にユーザーデータを取得
        User user = userDataAccess.findByCode(repUserCode);
        if (user == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }
        int status = 0;

        // 入力値をtaskオブジェクトにマッピング
        // (int code, String name, int status, User repUser)
        Task task = new Task(code, name, status,user);

        // saveメソッドを呼び出して、入力されたデータを保存
        taskDataAccess.save(task);

        // 新しくLogオブジェクトを作成
        // Logクラス (int taskCode, int changeUserCode, int status, LocalDate changeDate)
        Log log = new Log(code, loginUser.getCode(), status, LocalDate.now());
        // logs.csvにデータを1件新規登録
        logDataAccess.save(log);
        System.out.println(task.getName() + "の登録が完了しました。");
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code      タスクコード
     * @param status    新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status, User loginUser) throws AppException {
        Task task = taskDataAccess.findByCode(code);
        
        // AppExceptionの例外を書く
        if(task == null){
            //入力されたタスクコードが `tasks.csv`に存在しない場合
            throw new AppException("存在するタスクコードを入力してください");
        }
            //  スローするときのメッセージは「ステータスは、前のステータスより1つ先のもののみを選択してください」
            // 「未着手」から「完了」
            if(( task.getStatus() == 0) == (status  == 2)){
                throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
                // 「着手中」から「着手中」
            }else if(( task.getStatus() == 1) == (status  == 1  )){
                throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
                // 「完了」から他のステータス
            }else if(( task.getStatus() == 2) == (status  == 2 ||  status == 1 )){
                throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
            }
            // `tasks.csv`の該当タスクのステータスを変更後のステータスに更新
            // (int code, String name, int status, User repUser)
            Task taskUpdate = new Task(code, task.getName(), status, loginUser);
            taskDataAccess.update(taskUpdate);
            // `logs.csv`にデータを1件作成する
            // (int taskCode, int changeUserCode, int status, LocalDate changeDate)
            
            Log log = new Log(code,  loginUser.getCode(), status,LocalDate.now());
            logDataAccess.save(log);
            System.out.println( task.getName() + "の変更が完了しました。");
        
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}