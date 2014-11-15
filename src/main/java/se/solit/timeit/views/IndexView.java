package se.solit.timeit.views;

import com.google.common.base.Charsets;

import io.dropwizard.views.View;

public class IndexView extends View
{
	public IndexView()
	{
		super("index.ftl", Charsets.UTF_8);
	}
}
