package com.swingsword.ssengine.bar;

import net.minecraft.server.v1_10_R1.Entity;
import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.World;

public class DummyEntity extends Entity {
	public DummyEntity(World world) {
		super(world);
	}

	@Override
	protected void a(NBTTagCompound nbttagcompound) {
	}

	@Override
	protected void b(NBTTagCompound nbttagcompound) {
	}

	@Override
	public MinecraftServer h() {
		return null;
	}

	@Override
	protected void i() {
	}
}