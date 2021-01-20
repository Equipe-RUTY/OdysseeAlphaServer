package application;

import javafx.animation.AnimationTimer;

public class UpdateTimer extends AnimationTimer {
	
	private ExpoController control;
	
	public UpdateTimer(ExpoController control) {
		this.control = control;
	}

	
	@Override
	public void handle(long now) {
		control.updateVisiteurs();
	}
}
