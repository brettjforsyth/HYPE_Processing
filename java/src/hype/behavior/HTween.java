package hype.behavior;

import hype.drawable.HDrawable;
import hype.interfaces.HCallback;
import hype.util.H;
import hype.util.HConstants;
import hype.util.HMath;
import processing.core.PApplet;
import processing.core.PVector;

public class HTween extends HBehavior {
	protected HDrawable _target;
	protected HCallback _callback;
	protected float _currVal, _dVal, _endVal,
		_spring, _ease,
		_origVal1, _origVal2, _dirRad;
	protected int _propertyId;
	
	public HTween() {
		_propertyId = HConstants.X;
		_ease = 1;
	}
	
	protected void updateOrigVal() {
		if(_target == null) return;
		
		switch(_propertyId) {
		case HConstants.LOCATION:
			_origVal1 = _target.x();
			_origVal2 = _target.y();
			break;
		case HConstants.SCALE:
			_origVal1 = _target.width();
			_origVal2 = _target.height();
			break;
		}
	}
	
	public HTween target(HDrawable d) {
		if(d == null) unregister();
		else register();
		_target = d;
		return this;
	}
	
	public HDrawable target() {
		return _target;
	}
	
	public HTween callback(HCallback c) {
		_callback = c;
		return this;
	}
	
	public HCallback callback() {
		return _callback;
	}
	
	public HTween start(float f) {
		_currVal = f;
		updateOrigVal();
		return this;
	}
	
	public HTween start(float x, float y) {
		_origVal1 = x;
		_origVal2 = y;
		_currVal = 0;
		return this;
	}
	
	public float curr() {
		return _currVal;
	}
	
	@SuppressWarnings("static-access")
	public PVector currPt() {
		PApplet app = H.app();
		float x = _origVal1 + app.cos(_dirRad)*_currVal;
		float y = _origVal2 + app.sin(_dirRad)*_currVal;
		return new PVector(x,y);
	}
	
	public HTween end(float f) {
		_endVal = f;
		updateOrigVal();
		return this;
	}
	
	@SuppressWarnings("static-access")
	public HTween end(float x, float y) {
		_endVal = H.app().dist(_origVal1,_origVal2, x,y);
		_dirRad = HMath.xAxisAngle(_origVal1,_origVal2, x,y);
		return this;
	}
	
	public float end() {
		return _endVal;
	}
	
	@SuppressWarnings("static-access")
	public PVector endPt() {
		PApplet app = H.app();
		float x = _origVal1 + app.cos(_dirRad)*_endVal;
		float y = _origVal2 + app.sin(_dirRad)*_endVal;
		return new PVector(x,y);
	}
	
	public HTween spring(float f) {
		_spring = f;
		return this;
	}
	
	public float spring() {
		return _spring;
	}
	
	public HTween ease(float f) {
		_ease = f;
		return this;
	}
	
	public float ease() {
		return _ease;
	}
	
	public HTween property(int id) {
		_propertyId = id;
		updateOrigVal();
		return this;
	}
	
	public float property() {
		return _propertyId;
	}
	
	public float nextVal() {
		_dVal = _dVal*_spring + (_endVal-_currVal)*_ease;
		_currVal += _dVal;
		return _currVal;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void runBehavior(PApplet app) {
		if(_target == null) return;
		
		float val = nextVal();
		
		switch(_propertyId) {
		case HConstants.WIDTH:		_target.width(val);				break;
		case HConstants.HEIGHT:		_target.height(val);			break;
		case HConstants.SIZE:		_target.size(val);				break;
		case HConstants.ALPHA:		_target.alpha(app.round(val));	break;
		case HConstants.X:			_target.x(val);					break;
		case HConstants.Y:			_target.y(val);					break;
		case HConstants.LOCATION:
			float x = _origVal1 + app.cos(_dirRad)*val;
			float y = _origVal2 + app.sin(_dirRad)*val;
			_target.loc(x,y);
			break;
		case HConstants.ROTATION:	_target.rotation(val);			break;
		case HConstants.DROTATION:	_target.rotate(val);			break;
		case HConstants.DX:			_target.move(val,0);			break;
		case HConstants.DY:			_target.move(0,val);			break;
		case HConstants.DLOC:		_target.move(val,val);			break;
		case HConstants.SCALE:
			float w = _origVal1 * val;
			float h = _origVal2 * val;
			_target.size(w,h);
			break;
		default: break;
		}
		
		float tolerance = 1f/512;
		if(_endVal-tolerance<val && val<_endVal+tolerance &&
			app.abs(_dVal)<tolerance
		) {
			unregister();
			if(_callback != null) _callback.run(val);
		}
	}
	
	@Override
	public HTween register() {
		return (HTween) super.register();
	}
	
	@Override
	public HTween unregister() {
		return (HTween) super.unregister();
	}
}