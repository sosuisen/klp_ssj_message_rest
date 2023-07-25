package com.example.model.validator;

import jakarta.validation.groups.Default;

/**
 *  バリデーショングループの設定
 *  UpdateChecksグループはDefaultグループを継承、
 *  この結果、UpdateChecksグループが有効なときは
 *  Defaultグループも有効となります。
 */
public interface UpdateChecks extends Default{

}
