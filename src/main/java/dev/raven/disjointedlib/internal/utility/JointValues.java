package dev.raven.disjointedlib.internal.utility;

import dev.raven.disjointedlib.DisjointedLib;
import net.minecraft.nbt.CompoundTag;
import org.valkyrienskies.core.impl.shadow.FL;
import org.valkyrienskies.core.internal.joints.VSJoint;
import org.valkyrienskies.core.internal.joints.VSJointMaxForceTorque;
import org.valkyrienskies.physics_api.joints.MaxForceTorque;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class JointValues<J extends VSJoint> extends EnumMap<JointUtility.JointValue, Object> {
    public JointValues() {
        super(JointUtility.JointValue.class);
    }

    public abstract Class<J> getJointClass();

    public abstract List<JointUtility.JointValue> validValues();

    @Override
    public Object put(JointUtility.JointValue key, Object value) {
        if (validValues().contains(key)) return super.put(key, value);
        DisjointedLib.LOGGER.warn("Cannot put value for {}, is not valid for {}", key.name(), getJointClass().getSimpleName());
        return null;
    }

    public JointValues<J> withChanged(Entry<JointUtility.JointValue, Object>... changedValues) {
        for (Entry<JointUtility.JointValue, Object> changed : changedValues) {
            this.put(changed.getKey(), changed.getValue());
        }
        return this;
    }

    public JointValues<J> withChanged(JointUtility.JointValue key, Object value) {
        put(key, value);
        return this;
    }

    public void fillRemaining() {
        for (JointUtility.JointValue value : validValues())
            if (!this.containsKey(value))
                putDefault(value);
    }

    public void fillFromTag(CompoundTag tag) {
        for (JointUtility.JointValue value : validValues()) {
            put(value, getForType(tag, value)); // might break idk
        }
    }

    public Object getForType(CompoundTag tag, JointUtility.JointValue value) {
        Class<?> type = value.type; // unless i wanna force users to use jdk 21+ this is probably the best way to do this
        if (type == Float.class)
            return tag.getFloat(value.name());
        else if (type == Double.class)
            return tag.getDouble(value.name());
        else if (type == VSJointMaxForceTorque.class)
            return new VSJointMaxForceTorque(tag.getFloat(value.name() + "f"), tag.getFloat(value.name() + "t"));
        else
            return tag.get(value.name());
    }

    public Object putDefault(JointUtility.JointValue key) {
        return this.put(key, switch (key) {
            case COMPLIANCE -> DEFAULT_COMPLIANCE;
            case STIFFNESS -> DEFAULT_STIFFNESS;
            case TOLERANCE -> DEFAULT_TOLERANCE;
            case MINLENGTH -> DEFAULT_MINLENGTH;
            case DAMPING -> DEFAULT_DAMPING;
            default -> null;
        });
    }

    public static final Double DEFAULT_COMPLIANCE = VSJoint.DEFAULT_COMPLIANCE;
    public static final Float DEFAULT_STIFFNESS = 1e8f;
    public static final Float DEFAULT_TOLERANCE = 0.1f;
    public static final Float DEFAULT_MINLENGTH = 0f;
    public static final Float DEFAULT_DAMPING = null;
}
