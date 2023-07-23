package com.example;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import jakarta.ws.rs.ApplicationPath;

/**
 * このアプリを設定するために必須のクラスです。
 * JAX-RSでは、jakarta.ws.rs.core.Applicationのサブクラスである
 * ResourceConfigを用いて、リソースの存在するパッケージ（com.example.controller）を
 * 指定する必要があります。
 * 
 * @ApplicationPath は、このアプリが呼ばれるURLを指定するパスで、
 * コンテキストルート（通常はプロジェクト名）からの相対パスを書きます。
 * "/msg"を指定した場合のURLの例） http://localhost:8080/プロジェクト名/msg
 * 
 * パスの先頭の/と末尾の/はあってもなくても同じです。
 */
@ApplicationPath("/api")
public class MyApplication extends ResourceConfig {
	public MyApplication() {
		packages("com.example.resources");
		// @RolesAllowed 等のアノテーションでアクセス制御するために必要
		register(RolesAllowedDynamicFeature.class);
	}
}
