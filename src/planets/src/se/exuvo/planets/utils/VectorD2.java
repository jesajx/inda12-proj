package se.exuvo.planets.utils;

import com.artemis.utils.FastMath;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Class representing an mutable 2d-vector with double-precision.
 * @author Jesajx
 */
public class VectorD2 {
	
	/** Parameter. */
	public double x, y;
	
	/**
	 * Creates a zero-VectorD2:
	 * Same as {@code VectorD2(0d, 0d)}
	 */
	public VectorD2() {}
	
	/**
	 * Creates a VectorD2 with the given parameters.
	 * @param x value to set {@code this.x} to.
	 * @param y value to set {@code this.y} to.
	 */
	public VectorD2(double x, double y) {
		set(x, y);
	}
	
	/**
	 * Creates a copy of this given VectorD2.
	 * @param v VectorD2 to copy.
	 */
	public VectorD2(VectorD2 v) {
		this(v.x, v.y);
	}
	
	
	/**
	 * Copies this VectorD2.
	 * @return a copy of this VectodD2.
	 */
	public VectorD2 cpy() {
		return new VectorD2(this);
	}
	
	
	
	
	/**
	 * Adds the given VectorD2 to this VectorD2.
	 * @param v vector to add.
	 * @return a reference to this VectorD2.
	 * @see #add(double, double)
	 */
	public VectorD2 add(VectorD2 v) {
		return add(v.x, v.y);
	}
	
	/**
	 * Subtracts the given VectorD2 form this VectorD2.
	 * @param v vector to subtract.
	 * @return a reference to this VectorD2.
	 * @see #sub(double, double)
	 */
	public VectorD2 sub(VectorD2 v) {
		return sub(v.x, v.y);
	}

	/**
	 * Sets the parameters of this VectorD2
	 * to that of the given.
	 * @param v VectorD2 to copy.
	 * @return a reference to this VectorD2.
	 * @see #set(double, double)
	 */
	public VectorD2 set(VectorD2 v) {
		return set(v.x, v.y);
	}


	

	/**
	 * Adds the arguments to respective parameters of this VectorD2:
	 * {@code this.x += x; this.y += y}
	 * @param x value to add to {@code this.x}.
	 * @param y value to add to {@code this.y}.
	 * @return a reference to this VectorD2.
	 */
	public VectorD2 add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	/**
	 * Subtracts the arguments from the respective parameters of this VectorD2.
	 * Same as {@code add(-x, -y)}.
	 * @param x value to subtract from {@code this.x}.
	 * @param y value to subtract from {@code this.y}.
	 * @return a reference to this VectorD2.
	 * @see #add(double, double)
	 */
	public VectorD2 sub(double x, double y) {
		return add(-x, -y);
	}
		
	/**
	 * Sets the parameters of this VectorD2.
	 * @param x value to set {@code this.x} to.
	 * @param y value to set {@code this.y} to.
	 * @return a reference to this VectorD2.
	 */
	public VectorD2 set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	
	/**
	 * Negates this VectorD2,
	 * so that it points in the opposite direction.
	 * @return a reference to this VectorD2.
	 */
	public VectorD2 neg() {
		x = -x;
		y = -y;
		return this;
	}
	
	
	
	/**
	 * Multiplies both parameters of this VectorD2 with the given value:
	 * {@code x *= k; y *= k}.
	 * @param k value to multiply with.
	 * @return a reference to this VectorD2.
	 */
	public VectorD2 mul(double k) {
		this.x *= k;
		this.y *= k;
		return this;
	}
	
	/**
	 * Divides both parameters of this vector with the given value:
	 * {@code x /= k; y /= k}.
	 * Exactly the as {@code mul(1/k)}.
	 * @param k value to divide with.
	 * @return a reference to this VectorD2.
	 */
	public VectorD2 div(double k) {
		return mul(1/k);
	}
	
	
	
	
	/**
	 * Returns the length squared of this VectorD2.
	 * This is faster than {@code #len()}.
	 * Same as {@code this.dot(this)} (but probably faster).
	 * @return the length squared: len<sup>2</sup>.
	 */
	public double len2() {
		return x*x + y*y;
	}
	
	/**
	 * Returns the length of this VectorD2.
	 * This uses the squareroot, so might be slow.
	 * Same as {@code sqrt(v.len2())}.
	 * @return the length.
	 */
	public double len() {
		return FastMath.sqrt(len2());
	}
	
	
	/**
	 * Rotates this VectorD2 90-degrees counter-clockwise.
	 * @return a reference to this VectorD2.
	 */
	public VectorD2 rotCC() {
		final double temp = y;
		y = x;
		x = -temp;
		return this;
	}
	
	/**
	 * Rotates this VectorD2 90-degrees clockwise.
	 * @return a reference to this VectorD2.
	 */
	public VectorD2 rotC() {
		final double temp = y;
		y = -x;
		x = temp;
		return this;
	}
	
	/**
	 * Normalizes this vector:
	 * {@code div(len())} (except faster).
	 * @return a reference to this VectorD2.
	 */
	public VectorD2 nor() {
		final double invlen = FastMath.inverseSqrt(len2());
		return invlen == 0d ? this : mul(invlen);
	}
	
	/**
	 * Returns the dot-product/inner-product
	 * of this and the given VectorD2.
	 * @param v vector to compute the dot-product with.
	 * @return {@code x*v.x + y*v.y}
	 */
	public double dot(VectorD2 v) {
		return (x * v.x) + (y * v.y);
	}
	
	/**
	 * Returns the distance squared
	 * between this and the given VectorD2.
	 * This method is faster than {@link #dst(VectorD2)}.
	 * @param v the other vector.
	 * @return the distance squared.
	 */
	public double dst2(VectorD2 v) {
		final double dx = v.x - x;
		final double dy = v.y - y;
		return dx*dx + dy*dy;
	}
	
	/**
	 * Returns the distance 
	 * between this and the given VectorD2.
	 * This method is slower than {@link #dst2(VectorD2)},
	 * since this method takes the square root
	 * of the that.
	 * @param v the other vector.
	 * @return the distance.
	 */
	public double dst(VectorD2 v) {
		return FastMath.sqrt(dst2(v));
	}
	
	
	/**
	 * Returns a VectorD2 that is
	 * {@code v-this}.
	 * @param v the other vector.
	 * @return a NEW vector representing
	 * the difference from this vector to v.
	 */
	public VectorD2 to(VectorD2 v) {
		return new VectorD2(v.x-x, v.y-y);
	}
	
	
	/**
	 * Returns a String representation of this VectorD2
	 * in the format "[x; y]".
	 */
	@Override
	public String toString() {
		return "[" + x + "; " + y + "]";
	}
	
	/**
	 * Creates a Vector2 with the parameters of this VectorD2.
	 * Precision may be lost!
	 * @return a Vector2.
	 */
	public Vector2 toVector2() {
		return new Vector2((float)x, (float)y);
	}
}
