package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.taskapp.exception.AppException;
import com.taskapp.model.User;

public class UserDataAccess {
    private final String filePath;

    public UserDataAccess() {
        filePath = "app/src/main/resources/users.csv";
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * 
     * @param filePath
     */
    public UserDataAccess(String filePath) {
        this.filePath = filePath;
    }

    /**
     * メールアドレスとパスワードを基にユーザーデータを探します。
     * 
     * @param email    メールアドレス
     * @param password パスワード
     * @return 見つかったユーザー
     */
    public User findByEmailAndPassword(String email, String password) {
        User user = null;
        // メールアドレスとパスワードを基にデータを探す
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // タイトル行を読み飛ばす
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // メールアドレス、パスワードが一致しなかったらスキップ
                if (!(values[2].equals(email) && values[3].equals(password)))
                    continue;
                // Userオブジェクト(int code, String name, String email, String password)
                int code = Integer.parseInt(values[0]);
                String name = values[1];
                String userEmail = values[2];
                String userPassword = values[3];
                user = new User(code, name, userEmail, userPassword);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ;
        }
        return user;
    }

    /**
     * コードを基にユーザーデータを取得します。
     * 
     * @param code 取得するユーザーのコード
     * @return 見つかったユーザー
     */
    public User findByCode(int code) throws AppException {
        User user = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // タイトル行を読み飛ばす
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                int userCode = Integer.parseInt(values[0]);
                if (code != userCode)
                    continue;

                // Userオブジェクト(int code, String name, String email, String password)
                String name = values[1];
                String userEmail = values[2];
                String userPassword = values[3];
                user = new User(code, name, userEmail, userPassword);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }
}
