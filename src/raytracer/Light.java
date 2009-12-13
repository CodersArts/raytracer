package raytracer;

import java.awt.Color;

public class Light {
	public final Point location;
	public final Color color;
	final float a, b, c;

	public Light(Point location, Color color, float a, float b, float c) {
		this.location = location;
		this.color = color;
		this.a = a;
		this.b = b;
		this.c = c;
	}

	/**
	 *
	 * @param d - distance
	 * @return attenuation factor at distance d
	 */
	public float getAttenuationFactor(double d) {
		return 1 / (float)(a + b*c + c*(d*d));
	}

	public Color getColor(RayHit hit, Ray lightRay) {
		double distance = lightRay.origin.distanceTo(location);
		Log.debug("  distance      = " + distance);
		float attenuationFactor = getAttenuationFactor(distance);
		Log.debug("  attenuation   = " + attenuationFactor);

		// diffuse
		Log.debug("  normal vector = " + hit.normal);
		Log.debug("  light vector  = " + lightRay.direction);
		float diffuseStrength = hit.shape.finish.diff * (float)Math.max(0.0, hit.normal.dot(lightRay.direction));
		Log.debug("  diff strength = " + diffuseStrength);

		// specular
		Vector halfway = Vector.halfway(hit.ray.direction, lightRay.direction);
		Log.debug("  halfway vector= " + halfway);
		Vector r = lightRay.direction.minus(hit.normal.times(2.0*lightRay.direction.dot(hit.normal)));
		Log.debug("  r             = " + r);
		float specularStrength = hit.shape.finish.spec * (float)Math.pow(Math.max(0.0, hit.ray.direction.dot(r)), hit.shape.finish.shiny);
		Log.debug("  spec strength = " + specularStrength);

		float[] shapeColor = hit.shape.getColor(hit.point).getRGBColorComponents(new float[3]);
		float[] intensity = color.getRGBColorComponents(new float[3]);
		float red = intensity[0] * attenuationFactor * (shapeColor[0] * diffuseStrength + specularStrength);
		float green = intensity[1] * attenuationFactor * (shapeColor[1] * diffuseStrength + specularStrength);
		float blue = intensity[2] * attenuationFactor * (shapeColor[2] * diffuseStrength + specularStrength);

		Log.debug("  final color   = (" + red + ", " + green + ", " + blue + ")");

		return new Color(ColorUtil.clamp(red), ColorUtil.clamp(green), ColorUtil.clamp(blue));
	}
}
