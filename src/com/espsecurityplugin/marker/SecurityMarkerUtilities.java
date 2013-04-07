package com.espsecurityplugin.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IMarkerUpdater;

public class SecurityMarkerUtilities implements IMarkerUpdater {

	@Override
	public String[] getAttribute() {
		return null;
	}

	@Override
	public String getMarkerType() {
		return null;
	}

	@Override
	public boolean updateMarker(IMarker marker, IDocument doc, Position position) {
		try {
			marker.setAttribute(IMarker.CHAR_START, position.getOffset());
			marker.setAttribute(IMarker.CHAR_END, position.getLength()+position.getOffset());
			return true;
		} catch (CoreException e) {
			return false;
		}
	}
}
