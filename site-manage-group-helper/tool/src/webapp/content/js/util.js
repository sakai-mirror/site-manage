function setStateValue()
{
	// concatenate the group member option values
	var stateValue = "";
	var values = document.getElementsByName('groupMembers-selection');
	for (var i=0; i<values.length;i++)
	{
		//alert(values[i].value);
	}
	alert("sate=" + stateValue);
	document.getElementById('content::state-init').value = stateValue;
	}
}