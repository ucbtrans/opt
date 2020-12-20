create_new_project	Make a new project
create_new_project_2com	Make a new project, add description, add second commodity, change duration
demand			Add demand, expecting 5000 veh/hr (below capacity=6000)
on_ramp_at_cap		Add on-ramp with 1000veh/hr demand and expecting mainline flow 6000 veh/hr, exactly at capacity
on_ramp_above_cap	Add on-ramp with 1100veh/hr demand at 2:00 and expecting congestion since demand=6100veh/hr > cap=6000veh/hr
on_ramp_AUX.opt		Add AUX lane with on-ramp. Expecting to see flow in Aux-lane (1100 veh/hr)
on_off_ramp_AUX		Add AUX lane with on and off-ramp. Expecting to see flow in Aux-lane and at the off-ramp (1100 veh/hr)
off_ramp_AUX		Add AUX lane with off-ramp. Expecting to see 1100 veh/hr on the off-ramp
off_ramp_AUX_split_ratio	Add AUX lane with off-ramp using split ratio. Expecting to see 500 veh/hr on the off-ramp and AUX lane
on_off_ramp_AUX_split_ratio	Add AUX lane with on and off-ramp using split ratio. Expecting to see 600 veh/hr on the off-ramp and 1000 on the AUX lane
multiple_off_ramps_split	Add multiple off-ramps with split ratios. Expecting on all of them 600 veh/hr
multiple_off_ramps_hov_split	Add multiple off-ramps with split ratios and hov. Expecting on all of them 600 veh/hr total (sum of GP and HOV)
inner_outer_off_ramp		Expecting 600 veh/hr in outer off-ramp and 1000 veh/hr on inner off-ramp
on_ramp_fixed_meter	Add fixed ramp meter at 500 veh/hr for GP and 400 veh/hr for HOV
on_ramp_fixed_meter_queue_override	Add fixed ramp meter at 500 veh/hr for GP and 400 veh/hr for HOV but with queue override (700 veh/hr GP and 500 veh/hr ML)
on_ramp_alinea_meter	Add alinea ramp meter at 500 veh/hr maximum at GP and fixed on HOV at 400 veh/hr


