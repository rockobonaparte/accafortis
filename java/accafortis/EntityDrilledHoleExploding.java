package accafortis;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by Adam on 5/19/14.
 */
public class EntityDrilledHoleExploding extends Entity {
    /** How long the fuse is */
    public int fuse;

    /** Whether or not the TNT has exploded */
    private boolean hasExploded = false;

    private int blockToDrop;

    public EntityDrilledHoleExploding(World world, int blockToDrop)
    {
        super(world);
        this.blockToDrop = blockToDrop;
        fuse = 0;
        preventEntitySpawning = true;
        setSize(0.98F, 0.98F);
        yOffset = height / 2.0F;
    }

    public EntityDrilledHoleExploding(World world, double x, double y, double z, int blockToDrop)
    {
        this(world, blockToDrop);

        setPosition(x, y, z);

        float randPi = (float)(world.rand.nextFloat()*Math.PI*2);

//        motionX = -(Math.sin(randPi))*0.02F;
//        motionY = 0.2;
//        motionZ = -(Math.cos(randPi))*0.02F;

        motionX = 0.0F;
        motionY = 0.0F;
        motionZ = 0.0F;

        fuse = 60;

        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    @Override
    protected void entityInit() {}

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }

    @Override
    public boolean canBePushed()
    {
        return true;
    }

    @Override
    public void onUpdate()
    {
//        prevPosX = posX;
//        prevPosY = posY;
//        prevPosZ = posZ;
//
//        motionY -= 0.04;
//
//        moveEntity(motionX, motionY, motionZ);
//
//        motionX *= 0.98;
//        motionY *= 0.98;
//        motionZ *= 0.98;
//
//        if(onGround)
//        {
//            motionX *= 0.7;
//            motionZ *= 0.7;
//            motionY *= -0.5;
//        }

        if(fuse-- <= 0)
        {
            if(!worldObj.isRemote)
            {
                setDead();
                explode();
            }
            else {
                if(hasExploded)
                {
                    setDead();
                }
                else {
                    worldObj.spawnParticle("lava", posX, posY + 0.5, posZ, 0, 0, 0);
                }
            }
        }
        else {
            worldObj.spawnParticle("lava", posX, posY + 0.5, posZ, 0, 0, 0);
        }
    }

    private void explode()
    {
        // Overridding worldObj.createExplosion(null, posX, posY, posZ, 5.0f, true);
        // I'm doing my own explosion!


//	    /**
//	     * Creates an explosion. Args: entity, x, y, z, strength
//	     */
//	    public Explosion createExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9)
//	    {
//	        return this.newExplosion(par1Entity, par2, par4, par6, par8, false, par9);
//	    }
//
//	    /**
//	     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
//	     */
//	    public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9, boolean par10)
//	    {
//	        Explosion explosion = new Explosion(this, par1Entity, par2, par4, par6, par8);
//	        explosion.isFlaming = par9;
//	        explosion.isSmoking = par10;
//	        explosion.doExplosionA();
//	        explosion.doExplosionB(true);
//	        return explosion;
//	    }

        MiningTNTExplosion explosion = new MiningTNTExplosion(worldObj, this, posX, posY, posZ, 1.0001f);
        explosion.isFlaming = false;			// Almost set that to true.  LOL!  Burning dynamite whooo!
        explosion.isSmoking = true;
        explosion.doExplosionA();
        explosion.doExplosionB(true);

        // I Know right now this won't do exactly what I want (drop a block in place of this entity, but it's a start.
//        worldObj.setBlock((int) posX, (int) posY, (int) posZ, blockToDrop);
//        worldObj.destroyBlock((int) posX, (int) posY, (int) posZ, true);

        hasExploded = true;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbtTags)
    {
        nbtTags.setByte("Fuse", (byte)fuse);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbtTags)
    {
        fuse = nbtTags.getByte("Fuse");
    }

    @Override
    public float getShadowSize()
    {
        return 0;
    }
}
