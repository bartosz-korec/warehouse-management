package com.bartoszkorec.warehouse_management;

import org.springframework.boot.SpringApplication;

public class TestWarehouseManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(WarehouseManagementApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
