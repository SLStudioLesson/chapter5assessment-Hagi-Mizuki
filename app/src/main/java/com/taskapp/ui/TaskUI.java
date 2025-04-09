package com.taskapp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.taskapp.exception.AppException;
import com.taskapp.logic.TaskLogic;
import com.taskapp.logic.UserLogic;
import com.taskapp.model.User;

public class TaskUI {
    private final BufferedReader reader;

    private final UserLogic userLogic;

    private final TaskLogic taskLogic;

    private User loginUser;

    public TaskUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        userLogic = new UserLogic();
        taskLogic = new TaskLogic();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * 
     * @param reader
     * @param userLogic
     * @param taskLogic
     */
    public TaskUI(BufferedReader reader, UserLogic userLogic, TaskLogic taskLogic) {
        this.reader = reader;
        this.userLogic = userLogic;
        this.taskLogic = taskLogic;
    }

    /**
     * メニューを表示し、ユーザーの入力に基づいてアクションを実行します。
     *
     * @see #inputLogin()
     * @see com.taskapp.logic.TaskLogic#showAll(User)
     * @see #selectSubMenu()
     * @see #inputNewInformation()
     */
    public void displayMenu() throws AppException{
        System.out.println("タスク管理アプリケーションにようこそ!!");
        inputLogin();
        // メインメニュー
        boolean flg = true;
        while (flg) {
            try {
                System.out.println("以下1~3のメニューから好きな選択肢を選んでください。");
                System.out.println("1. タスク一覧, 2. タスク新規登録, 3. ログアウト");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();

                System.out.println();

                switch (selectMenu) {
                    case "1":
                        taskLogic.showAll(loginUser);
                        selectSubMenu();
                        break;
                    case "2":
                        // タスク新規登録
                        inputNewInformation(loginUser);
                        break;
                    case "3":
                        System.out.println("ログアウトしました。");
                        flg = false;
                        break;
                    default:
                        System.out.println("選択肢が誤っています。1~3の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのログイン情報を受け取り、ログイン処理を行います。
     *
     * @see com.taskapp.logic.UserLogic#login(String, String)
     */
    // ログインに必要な入力を受け付ける
    private void inputLogin() {
        boolean flg = true;
        while (flg) {
            try {
                System.out.print("メールアドレスを入力してください：");
                String email = reader.readLine();
                System.out.print("パスワードを入力してください：");
                String password = reader.readLine();

                // ログイン処理を呼び出す
                loginUser = userLogic.login(email, password);
                System.out.println();
                flg = false;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * ユーザーからの新規タスク情報を受け取り、新規タスクを登録します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#save(int, String, int, User)
     */
    // タスクの新規登録
    private void inputNewInformation(User loginUser)throws AppException {
        boolean flg = true;
        while (flg) {
            try {
                // 登録に必要な入力値を求める
                System.out.print("タスクコードを入力してください：");
                String taskCode = reader.readLine();
                // 整数以外が入力された場合
                if (!isNumber(taskCode)) {
                    System.out.println("コードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }

                System.out.print("タスク名を入力してください:");
                String taskName = reader.readLine();
                // タスク名は10文字以内ではない場合
                if (!(taskCode.length() <= 10)) {
                    System.out.println("タスク名は10文字以内で入力してください");
                    System.out.println();
                    continue;
                }

                int status = 0;

                System.out.print("担当するユーザーのコードを選択してください：");
                String repUser = reader.readLine();
                if (!isNumber(repUser)) {
                    System.out.println("コードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }


                // // // 担当者コードコードを基にユーザーデータを取得
                // User user = userDataAccess.findByCode(Integer.parseInt(repUser));
                // Logに保存
                // save /int code, String name, int repUserCode, User loginUser)
                taskLogic.save(Integer.parseInt(taskCode), taskName, Integer.parseInt(repUser), loginUser);
                flg = false;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
    }

    /**
     * タスクのステータス変更または削除を選択するサブメニューを表示します。
     *
     * @see #inputChangeInformation()
     * @see #inputDeleteInformation()
     */
    public void selectSubMenu() {
        boolean flg = true;
        while (flg) {
            try {
                System.out.println("以下1~2から好きな選択肢を選んでください");
                System.out.println("1. タスクのステータス変更, 2. メインメニューに戻る");
                System.out.print("選択: ");
                String selectMenu = reader.readLine();
                System.out.println();

                switch (selectMenu) {
                    case "1":
                        break;
                    case "2":
                        System.out.println("メインメニューに戻ります");
                        flg = false;
                    default:
                        System.out.println("選択に誤りがあります。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ユーザーからのタスクステータス変更情報を受け取り、タスクのステータスを変更します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#changeStatus(int, int, User)
     */
    public void inputChangeInformation() throws AppException{
        boolean flg = true;
        while(flg){

            try{
                // 変更情報に必要な入力情報を求める
                System.out.println("ステータスを変更するタスクコードを入力してください：");
                String  taskCode = reader.readLine();
                if(!isNumber(taskCode)){
                    System.out.println("コードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }
                // 仕様を満たさない場合、「コードは半角の数字で入力してください」
                System.out.print("どのステータスに変更するか選択してください。");
                System.out.println("1. 着手中, 2. 完了");
                System.out.print("選択肢：");
                String changeStatus = reader.readLine();
                if(!isNumber(changeStatus)){
                    //  仕様を満たさない場合、「ステータスは半角の数字で入力してください」
                    System.out.println("コードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }
                if(!(changeStatus.equals("1") || changeStatus.equals("2"))){
                    // 「ステータスは1・2の中から選択してください」
                    System.out.println("ステータスは1・2の中から選択してください");
                    System.out.println();
                    continue;
                }
                taskLogic.changeStatus(Integer.parseInt(taskCode), Integer.parseInt(changeStatus), loginUser);
                flg = false;
                // Logic.javaでAppException 入力されたタスクコードが `tasks.csv`に存在しない場合
                //  スローするときのメッセージは「ステータスは、前のステータスより1つ先のもののみを選択してください」
                //task Code,Name,Status,Rep_User_Code
    
            }catch(IOException e){
                System.err.println();
            }catch(AppException e){
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのタスク削除情報を受け取り、タスクを削除します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#delete(int)
     */
    // public void inputDeleteInformation() {
    // }

    /**
     * 指定された文字列が数値であるかどうかを判定します。
     * 負の数は判定対象外とする。
     *
     * @param inputText 判定する文字列
     * @return 数値であればtrue、そうでなければfalse
     */
    // 数値かどうか判定する
    private boolean isNumber(String inputText) {
        // 入力された文字列を分解し、全てが0~9の半角もしくは全角数字化どうかを判定する
        return inputText.chars().allMatch(c -> Character.isDigit((char) c));
    }
}