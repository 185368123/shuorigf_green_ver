package com.wcsmobile.data;

import com.wcsmobile.R;

public class ParamSettingDataStruct {

	public final static int NORMAL_CAPCITY_OF_BATTERY_OFFSET=0x1;
	public final static int SYSTEM_VOLTAGE_OFFSET=0x2;
	public final static int BATTERY_TYPE_OFFSET=0x3;
	public final static int OVER_VOLTAGE_VOLTAGE_OFFSET=0x4;
	public final static int CHARGE_LIMIT_VOLTAGE_OFFSET=0x5;
	public final static int BALANCED_CHARGE_VOLTAGE_OFFSET=0x6;
	public final static int LEFT_CHARGE_VOLTAGE_OFFSET=0x7;
	public final static int FLOAT_CHARGE_VOLTAGE_OFFSET=0x8;
	public final static int LIFT_CHARGE_RETURN_VOLTAGE_OFFSET=0x9;
	public final static int OVER_DISCHARGE_RETURN_VOLTAGE_OFFSET=0xA;
	public final static int WARNING_UNDER_VOLTAGE_OFFSET=0xB;
	public final static int OVER_DISCHARGE_VOLTAGE_OFFSET=0xC;
	public final static int DISCHARGE_LIMIT_VOLTAGE_OFFSET=0xD;
	public final static int OVER_DISCHARGE_DELAY_TIME=0xF;
	public final static int EQUALIZATION_CHARAGING_TIME_OFFSET=0x10;
	public final static int PROMOTE_CHARGING_TIME_OFFSET=0x11;
	public final static int EQUILIBRIUM_CHARGE_INTERVAL=0x12;
	public final static int TIMEPERATURE_COMPENSTATION_COEFFICIENT_OFFSET=0x13;
	public final static int FIRST_WORKING_TIME_OFFSET=0x14;
	public final static int FIRST_WORKING_POWER_OFFSET=0x15;
	public final static int SECOND_WORKING_TIME_OFFSET=0x16;
	public final static int SECOND_WORKING_POWER_OFFSET=0x17;
	public final static int THIRD_WORKING_TIME_OFFSET=0x18;
	public final static int THIRD_WORKING_POWER_OFFSET=0x19;
	public final static int MORNING_WORKING_POWER_OFFSET=0x1A;
	public final static int MORNING_WORKING_TIME_OFFSET=0x1B;
	public final static int LOAD_OPERATIN_MODE_OFFSET=0x1C;
	public final static int OPTICAL_CONTROL_DELAY_TIME_OFFSET=0x1D;
	public final static int OPTICAL_CONTROL_VOLTAGE_OFFSET=0x1E;

	public final static int ID[]={
		-0xff,
		R.id.tv_parameter_set_nominal_capacity_of_battery_set_value,
		R.id.tv_parameter_set_system_voltage_set_value,
		R.id.tv_parameter_battery_type_set_value,
		R.id.tv_parameter_over_voltage_voltage_set_value,
		R.id.tv_parameter_charge_imit_oltage_set_value,
		R.id.tv_parameter_balanced_charge_voltage_set_value,
		R.id.tv_parameter_lift_charge_voltage_set_value,
		R.id.tv_parameter_float_charging_voltage_set_value,
		R.id.tv_parameter_lift_charge_return_voltage_set_value,
		R.id.tv_parameter_over_discharge_return_voltage_set_value,
		R.id.tv_parameter_warning_under_voltage_set_value,
		R.id.tv_parameter_over_discharge_voltage_set_value,
		R.id.tv_parameter_discharge_limit_voltage_set_value,
		-0xff,
		R.id.tv_parameter_over_discharge_delay_time_set_value,
		R.id.tv_parameter_equalization_charging_time_set_value,
		R.id.tv_parameter_promote_charging_time_set_value,
		R.id.tv_parameter_equilibrium_charge_interval_set_value,
		R.id.tv_parameter_temperature_compensation_coefficient_set_value,
		R.id.tv_parameter_first_working_time_set_value,
		R.id.tv_parameter_first_working_power_set_value,
		R.id.tv_parameter_second_working_time_set_value,
		R.id.tv_parameter_second_working_power_set_value,
		R.id.tv_parameter_third_working_time_set_value,
		R.id.tv_parameter_third_working_power_set_value,
		R.id.tv_parameter_morning_working_time_set_value,
		R.id.tv_parameter_morning_working_power_set_value,
		R.id.tv_parameter_load_operating_mode_set_value,
		R.id.tv_parameter_optical_control_delay_time_set_value,
		R.id.tv_parameter_optical_control_voltage_set_value,

	};
	public final static int DATA_OFFSET[]={
		0x0,
		NORMAL_CAPCITY_OF_BATTERY_OFFSET,
		SYSTEM_VOLTAGE_OFFSET,
		BATTERY_TYPE_OFFSET,
		OVER_VOLTAGE_VOLTAGE_OFFSET,
		CHARGE_LIMIT_VOLTAGE_OFFSET,
		BALANCED_CHARGE_VOLTAGE_OFFSET,
		LEFT_CHARGE_VOLTAGE_OFFSET,
		FLOAT_CHARGE_VOLTAGE_OFFSET,
		LIFT_CHARGE_RETURN_VOLTAGE_OFFSET,
		OVER_DISCHARGE_RETURN_VOLTAGE_OFFSET,
		WARNING_UNDER_VOLTAGE_OFFSET,
		OVER_DISCHARGE_VOLTAGE_OFFSET,
		DISCHARGE_LIMIT_VOLTAGE_OFFSET,
		0xE,
		OVER_DISCHARGE_DELAY_TIME,
		EQUALIZATION_CHARAGING_TIME_OFFSET,
		PROMOTE_CHARGING_TIME_OFFSET,
		EQUILIBRIUM_CHARGE_INTERVAL,
		TIMEPERATURE_COMPENSTATION_COEFFICIENT_OFFSET,
		FIRST_WORKING_TIME_OFFSET,
		FIRST_WORKING_POWER_OFFSET,
		SECOND_WORKING_TIME_OFFSET,
		SECOND_WORKING_POWER_OFFSET,
		THIRD_WORKING_TIME_OFFSET,
		THIRD_WORKING_POWER_OFFSET,
		MORNING_WORKING_POWER_OFFSET,
		MORNING_WORKING_TIME_OFFSET,
		LOAD_OPERATIN_MODE_OFFSET,
		OPTICAL_CONTROL_DELAY_TIME_OFFSET,
		OPTICAL_CONTROL_VOLTAGE_OFFSET,
		};

}
