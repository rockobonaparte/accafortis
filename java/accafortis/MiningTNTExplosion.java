package accafortis;

// This was taken from the vanilla explosion.  It's mostly a copy-paste of the deobfuscated Forge interpretation of it.
// I particularly changed the line where it determines if a block is deleted in the explosion.

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.Explosion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class MiningTNTExplosion extends Explosion
{
    /** whether or not the explosion sets fire to blocks around it */
    public boolean isFlaming;

    /** whether or not this explosion spawns smoke particles */
    public boolean isSmoking = true;
    private int explosionBlockScanLimit = 16;
    private Random explosionRNG = null;
    private World worldObj;
    public double explosionX;
    public double explosionY;
    public double explosionZ;
    public Entity exploder;
    public float explosionSize;

    /** A list of ChunkPositions of blocks affected by this explosion */
    public List affectedBlockPositions = new ArrayList();
    private Map field_77288_k = new HashMap();

    public MiningTNTExplosion(World par1World, Entity par2Entity, double x, double y, double z, float radius)
    {
    	super(par1World, par2Entity, x, y, z, radius);
        worldObj = par1World;
        exploder = par2Entity;
        explosionSize = radius;
        explosionX = x;
        explosionY = y;
        explosionZ = z;
        explosionRNG = worldObj.rand;
    }

    public void doNotNotchExplosion()
    {
        this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

        if (this.explosionSize >= 2.0F && this.isSmoking)
        {
            this.worldObj.spawnParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
        }
        else
        {
            this.worldObj.spawnParticle("largeexplode", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
        }

        int minExplodeX = (int) (this.explosionX - this.explosionSize);
        int maxExplodeX = (int) Math.ceil(this.explosionX + this.explosionSize);
        int minExplodeY = (int) (this.explosionY - this.explosionSize);
        int maxExplodeY = (int) Math.ceil(this.explosionY + this.explosionSize);
        int minExplodeZ = (int) (this.explosionZ - this.explosionSize);
        int maxExplodeZ = (int) Math.ceil(this.explosionZ + this.explosionSize);

        // A collision will be considered by the center point of each cube.
        for(double i = minExplodeX + 0.5; i <= maxExplodeX; ++i) {
            for(double j = minExplodeY + 0.5; j <= maxExplodeY; ++j) {
                for(double k = minExplodeZ + 0.5; k <= maxExplodeZ; ++k) {
                    double distance = Math.sqrt((this.explosionX - i) * (this.explosionX - i) +
                            (this.explosionY - j) * (this.explosionY - j) +
                            (this.explosionZ - k) * (this.explosionZ - k));

                    if(distance <= this.explosionSize) {
                        int int_i = (int) i, int_j = (int) j, int_k = (int) k;
                        int blockId = this.worldObj.getBlockId(int_i, int_j, int_k);

                        if (blockId > 0) {
                            Block block = Block.blocksList[blockId];

                            if (block.canDropFromExplosion(this)) {
                                // Right here is why I even started making this mod in the first place.  I just wanted my explosions to leave the stuff behind!
                                // That's it!
                                block.dropBlockAsItemWithChance(this.worldObj, int_i, int_j, int_k, this.worldObj.getBlockMetadata(int_i, int_j, int_k), 1.0F, 0);
                            }

                            block.onBlockExploded(this.worldObj, int_i, int_j, int_k, this);
                        }
                    }
                }
            }
        }
    }

    /**
     * Does the first part of the explosion (destroy blocks)
     */
    public void doExplosionA()
    {
        float savedExplosionSize = this.explosionSize;
        HashSet hashset = new HashSet();
        double d0;
        double d1;
        double d2;

        for (int i = 0; i < explosionBlockScanLimit; ++i)
        {
            for (int j = 0; j < explosionBlockScanLimit; ++j)
            {
                for (int k = 0; k < explosionBlockScanLimit; ++k)
                {
                    if (i == 0 || i == explosionBlockScanLimit - 1 || j == 0 || j == explosionBlockScanLimit - 1 || k == 0 || k == explosionBlockScanLimit - 1)
                    {
                        double d3 = (double)((float)i / ((float)explosionBlockScanLimit - 1.0F) * 2.0F - 1.0F);
                        double d4 = (double)((float)j / ((float)explosionBlockScanLimit - 1.0F) * 2.0F - 1.0F);
                        double d5 = (double)((float)k / ((float)explosionBlockScanLimit - 1.0F) * 2.0F - 1.0F);
                        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                        d3 /= d6;
                        d4 /= d6;
                        d5 /= d6;
                        float f1 = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                        d0 = this.explosionX;
                        d1 = this.explosionY;
                        d2 = this.explosionZ;

                        for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F)
                        {
                            int blockX = MathHelper.floor_double(d0);
                            int blockY = MathHelper.floor_double(d1);
                            int blockZ = MathHelper.floor_double(d2);
                            int k1 = this.worldObj.getBlockId(blockX, blockY, blockZ);

                            if (k1 > 0)
                            {
                                Block block = Block.blocksList[k1];
                                float f3 = this.exploder != null ? this.exploder.getBlockExplosionResistance(this, this.worldObj, blockX, blockY, blockZ, block) : block.getExplosionResistance(this.exploder, worldObj, blockX, blockY, blockZ, explosionX, explosionY, explosionZ);
                                f1 -= (f3 + 0.3F) * f2;
                            }

                            if (f1 > 0.0F && (this.exploder == null || this.exploder.shouldExplodeBlock(this, this.worldObj, blockX, blockY, blockZ, k1, f1)))
                            {
                                hashset.add(new ChunkPosition(blockX, blockY, blockZ));
                            }

                            d0 += d3 * (double)f2;
                            d1 += d4 * (double)f2;
                            d2 += d5 * (double)f2;
                        }
                    }
                }
            }
        }

        this.affectedBlockPositions.addAll(hashset);
        this.explosionSize *= 2.0F;
        int i = MathHelper.floor_double(this.explosionX - (double)this.explosionSize - 1.0D);
        int j = MathHelper.floor_double(this.explosionX + (double)this.explosionSize + 1.0D);
        int k = MathHelper.floor_double(this.explosionY - (double)this.explosionSize - 1.0D);
        int l1 = MathHelper.floor_double(this.explosionY + (double)this.explosionSize + 1.0D);
        int i2 = MathHelper.floor_double(this.explosionZ - (double)this.explosionSize - 1.0D);
        int j2 = MathHelper.floor_double(this.explosionZ + (double)this.explosionSize + 1.0D);
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getAABBPool().getAABB((double)i, (double)k, (double)i2, (double)j, (double)l1, (double)j2));
        Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.explosionX, this.explosionY, this.explosionZ);

        for (int k2 = 0; k2 < list.size(); ++k2)
        {
            Entity entity = (Entity)list.get(k2);
            double d7 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / (double)this.explosionSize;

            if (d7 <= 1.0D)
            {
                d0 = entity.posX - this.explosionX;
                d1 = entity.posY + (double)entity.getEyeHeight() - this.explosionY;
                d2 = entity.posZ - this.explosionZ;
                double d8 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);

                if (d8 != 0.0D)
                {
                    d0 /= d8;
                    d1 /= d8;
                    d2 /= d8;
                    double d9 = (double)this.worldObj.getBlockDensity(vec3, entity.boundingBox);
                    double d10 = (1.0D - d7) * d9;
                    entity.attackEntityFrom(DamageSource.setExplosionSource(this), (float)((int)((d10 * d10 + d10) / 2.0D * 8.0D * (double)this.explosionSize + 1.0D)));
                    double d11 = EnchantmentProtection.func_92092_a(entity, d10);
                    entity.motionX += d0 * d11;
                    entity.motionY += d1 * d11;
                    entity.motionZ += d2 * d11;

                    if (entity instanceof EntityPlayer)
                    {
                        this.field_77288_k.put((EntityPlayer)entity, this.worldObj.getWorldVec3Pool().getVecFromPool(d0 * d10, d1 * d10, d2 * d10));
                    }
                }
            }
        }

        this.explosionSize = savedExplosionSize;
    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
    public void doExplosionB(boolean par1)
    {
        this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

        if (this.explosionSize >= 2.0F && this.isSmoking)
        {
            this.worldObj.spawnParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
        }
        else
        {
            this.worldObj.spawnParticle("largeexplode", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
        }

        Iterator iterator;
        ChunkPosition chunkposition;
        int i;
        int j;
        int k;
        int l;

        // this.isSmoking is supposed to just determine if we should spawn smoke particles, but it looks like all the
        // beefy explosion code goes underneath it too.
        if (this.isSmoking)
        {
            iterator = this.affectedBlockPositions.iterator();

            while (iterator.hasNext())
            {
                chunkposition = (ChunkPosition)iterator.next();
                i = chunkposition.x;
                j = chunkposition.y;
                k = chunkposition.z;
                l = this.worldObj.getBlockId(i, j, k);

                if (par1)
                {
                    double d0 = (double)((float)i + this.worldObj.rand.nextFloat());
                    double d1 = (double)((float)j + this.worldObj.rand.nextFloat());
                    double d2 = (double)((float)k + this.worldObj.rand.nextFloat());
                    double d3 = d0 - this.explosionX;
                    double d4 = d1 - this.explosionY;
                    double d5 = d2 - this.explosionZ;
                    double d6 = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    double d7 = 0.5D / (d6 / (double)this.explosionSize + 0.1D);
                    d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
                    d3 *= d7;
                    d4 *= d7;
                    d5 *= d7;
                    this.worldObj.spawnParticle("explode", (d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D, (d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5);
                    this.worldObj.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
                }

                if (l > 0)
                {
                    Block block = Block.blocksList[l];

                    if (block.canDropFromExplosion(this))
                    {
                    	// Right here is why I even started making this mod in the first place.  I just wanted my explosions to leave the stuff behind!
                    	// That's it!
                        
                    	// block.dropBlockAsItemWithChance(this.worldObj, i, j, k, this.worldObj.getBlockMetadata(i, j, k), 1.0F / this.explosionSize, 0);
                        
                    	block.dropBlockAsItemWithChance(this.worldObj, i, j, k, this.worldObj.getBlockMetadata(i, j, k), 1.0F, 0);
                    }

                    block.onBlockExploded(this.worldObj, i, j, k, this);
                }
            }
        }

        // Set fire to blocks around explosion
        if (this.isFlaming)
        {
            iterator = this.affectedBlockPositions.iterator();

            while (iterator.hasNext())
            {
                chunkposition = (ChunkPosition)iterator.next();
                i = chunkposition.x;
                j = chunkposition.y;
                k = chunkposition.z;
                l = this.worldObj.getBlockId(i, j, k);
                int i1 = this.worldObj.getBlockId(i, j - 1, k);

                if (l == 0 && Block.opaqueCubeLookup[i1] && this.explosionRNG.nextInt(3) == 0)
                {
                    this.worldObj.setBlock(i, j, k, Block.fire.blockID);
                }
            }
        }
    }
}
