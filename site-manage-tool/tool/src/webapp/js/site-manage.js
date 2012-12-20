var sakai = sakai || {};
var utils = utils || {};


$.ajaxSetup({
    cache: false
});

/*
 calling template has dom placeholder for dialog,
 args:class of trigger, id of dialog, message strings
 */
sakai.getSiteInfo = function(trigger, dialogTarget, nosd, nold){
    utils.startDialog(dialogTarget);
    $("." + trigger).click(function(e){
        var siteURL = '/direct/site/' + $(this).attr('id') + '.json';
        jQuery.getJSON(siteURL, function(data){
            var desc = '', shortdesc = '', title = '', owner = '', email = '';
            if (data.description) {
                desc = unescape(data.description);
            }
            else {
                desc = nold;
            }
            if (data.shortDescription) {
                shortdesc = data.shortDescription;
            }
            else {
                shortdesc = nosd;
            }
            
            if (data.props) {
                if (data.props['contact-name']) {
                    owner = data.props['contact-name'];
                }
                
                if (data.props['contact-email']) {
                    email = " (<a href=\"mailto:" + data.props['contact-email'].escapeHTML() + "\" id=\"email\">" + data.props['contact-email'].escapeHTML() + "</a>)";
                }
            }
            sitetitle = data.title.escapeHTML();
            content = ("<h4><span id=\'owner\'></span>" + email + "</h4>" + "<br /><p class=\'textPanelFooter\' id=\'shortdesc\'>" + $(shortdesc).text() + "</p><br />" + "<div class=\"textPanel\">" + desc + "</div>");
            $("#" + dialogTarget).html(content);
            $("#" + dialogTarget + ' #shortdesc').text(shortdesc);
            $("#" + dialogTarget + ' #owner').text(owner);
            $("#" + dialogTarget).dialog('option', 'title', sitetitle);
            utils.endDialog(e, dialogTarget);
            return false;
        });
        
        
    });
};


/*
 calling template has dom placeholder for dialog,
 args:class of trigger, id of dialog, message strings
 */
sakai.getGroupInfo = function(trigger, dialogTarget, memberstr, printstr, tablestr1, tablestr2, tablestr3){
    utils.startDialog(dialogTarget);
    $('.' + trigger).click(function(e){
    
        var id = $(this).attr('id');
        var title = $('#group' + id).html();
        var groupURL = '/direct/membership/group/' + id + '.json';
        var list = "";
        var count = 1;
        
        jQuery.getJSON(groupURL, function(data){
            $.each(data.membership_collection, function(i, item){
                list = list + "<tr><td>" + count + ")&nbsp;" + item.userSortName + "</td><td>" + item.memberRole + "</td><td><a href=\'mailto:" + item.userEmail + "\'>" + item.userEmail + "</a></td></tr>";
                count = count + 1;
            });
            content = ("<h4>(<a  href=\"#\" id=\'printme\' class=\'print-window\' onclick=\'printPreview(\"/direct/membership/group/" + id + ".json\")\'>" + printstr + "</a>)</h4>" + "<p class=\'textPanelFooter\'></p>" + "<div class=\'textPanel\'><div id=\'groupListContent\'><table class=\'listHier lines nolines\' border=\'0\'><tr><th>" + tablestr1 + "</th><th>" + tablestr2 + "</th><th>" + tablestr3 + "</th>" + list + "</table></div>");
            $("#" + dialogTarget).html(content);
            $("#" + dialogTarget).dialog('option', 'title', memberstr + ': ' + title);
            utils.endDialog(e, dialogTarget);
            return false;
        });
    });
};

/*
 if message exists fade it in, apply the class, then hide
 args: message box id, class to apply
 */
sakai.setupMessageListener = function(messageHolder, messageMode){
    //test to see if there is an actual message (trim whitespace first)
    var str = $("#" + messageHolder).text();
    str = jQuery.trim(str);
    // show if message is there, then hide it
    if (str !== '') {
        $("#" + messageHolder).fadeIn('slow');
        $("#" + messageHolder).addClass(messageMode);
        $("#" + messageHolder).animate({
            opacity: 1.0
        }, 5000);
        $("#" + messageHolder).fadeOut('slow', function(){
            $("#" + messageHolder).remove();
        });
    }
};

/*
 a list with checkboxes, selecting/unselecting checkbox applies/removes class from row,
 selecting top checkbox selelects/unselects all, top checkbox is hidden if there are no
 selectable items, onload, rows with selected checkboxes are highlighted with class
 args: id of table, id of select all checkbox, highlight row class
 */
sakai.setupSelectList = function(list, allcontrol, highlightClass){
    $('#' + list + ' :checked').parent("td").parent("tr").addClass(highlightClass);
    
    if ($('#' + list + ' td :checkbox').length === 0) {
        $('#' + allcontrol).hide();
    }
    $('#' + allcontrol).click(function(){
        if (this.checked) {
            $('#' + list + ' :checkbox').attr('checked', 'checked');
            $('#' + list + ' :checkbox').parent('td').parent('tr').addClass(highlightClass);
        }
        else {
            $('#' + list + ' :checkbox').attr('checked', '');
            $('#' + list + ' tbody tr').removeClass(highlightClass);
        }
    });
    
    $('#' + list + ' :checkbox').click(function(){
        var someChecked = false;
        if (this.checked) {
            $(this).parents('tr').addClass(highlightClass);
        }
        else {
            $(this).parents('tr').removeClass(highlightClass);
        }
        $('#' + list + ' :checkbox').each(function(){
            if (this.checked) {
                someChecked = true;
            }
        });
        if (!someChecked) {
            $('#' + allcontrol).attr('checked', '');
        }
        if ($('#' + list + ' :checked').length !== $('#' + list + ' :checkbox').length) {
            $('#' + allcontrol).attr('checked', '');
        }
        
        if ($('#' + list + '  :checked').length === $('#' + list + '  :checkbox').length) {
            $('#' + allcontrol).attr('checked', 'checked');
        }
    });
};

sakai.siteTypeSetup = function(){

    $('input[name="itemType"]').attr('checked', '');
    $('#copy').click(function(e){
        $('#templateSettings').show();
        $('#buildOwn').attr('checked', '');
        $('#siteTypeList').hide();
        $('#termList').hide();
        utils.resizeFrame('grow');
        $('#submitFromTemplate').show();
        $('#submitBuildOwn').hide();
        $('#submitBuildOwn').attr('disabled', 'disabled');
        $('#copyContent').attr('checked', 'checked');
    });
    
    $('#buildOwn').click(function(e){
        $('#templateSettings').hide();
        $('#templateSettings input:checked').attr('checked', '');
        $('#siteTitleField').attr('value', '');
        $('input[id="copy"]').attr('checked', '');
        $('#templateSettings select').attr('selectedIndex', 0);
        $('#templateSettings span').hide();
        $('#siteTypeList').show();
        $('#submitFromTemplate').hide();
        $('#submitFromTemplate').attr('disabled', 'disabled');
        $('#submitFromTemplateCourse').hide();
        $('#submitBuildOwn').show();
        $('#nextInstructions span').hide();
        utils.resizeFrame('grow');
    });
    $('#siteTitleField').keyup(function(e){
        if ($(this).attr('value').length >= 1) {
            $('#submitFromTemplate').attr('disabled', '');
        }
        else {
            $('#submitFromTemplate').attr('disabled', 'disabled');
        }
    });
    $('#siteTitleField').blur(function(){
        if ($(this).attr('value').length >= 1) {
            $('#submitFromTemplate').attr('disabled', '');
        }
        else {
            $('#submitFromTemplate').attr('disabled', 'disabled');
        }
    });
    
    
    $('#selectTermTemplate').change(function(){
        if (this.selectedIndex === 0) {
            $('#submitFromTemplateCourse').attr('disabled', 'disabled');
        }
        else {
            $('#submitFromTemplateCourse').attr('disabled', '');
            
        }
    });
    
    $('#templateSiteId').change(function(){
        $('#submitFromTemplateCourse').attr('disabled', 'disabled');
        $('#submitFromTemplate').attr('disabled', 'disabled');
        if (this.selectedIndex === 0) {
            $('#templateSettings span').hide();
            $('#templateSettings select').attr('selectedIndex', 0);
            $('#submitFromTemplateCourse').attr('disabled', 'disabled');
            $('#siteTitleField').attr('value', '');
        }
        else {
        
            var type = $('#templateSiteId option:selected').attr('class');
            $('#templateSettings span').hide();
            $('#nextInstructions span').hide();
            if (type == "course") {
                $('#templateCourseInstruction').show();
                $('#submitFromTemplate').hide();
                $('#submitFromTemplateCourse').show();
                $('#siteTerms').show();
                $('#siteTitle').hide();
                $('#siteTerms select').focus();
                $('#siteTitleField').attr('value', '');
            }
            else {
                $('#submitFromTemplate').show();
                $('#submitFromTemplateCourse').hide();
                $('#templateNonCourseInstruction').show();
                $('#siteTitle').show();
                $('#siteTerms select').attr('selectedIndex', 0);
                $('#siteTitle input').focus();
            }
        }
    });
    $('#siteTypeList input').click(function(e){
        if ($(this).attr('id') == 'course') {
            $('#termList').show();
        }
        else {
            $('#termList').hide();
        }
        $('#submitBuildOwn').attr('disabled', '');
        
    });
};

sakai.setupToggleAreas = function(toggler, togglee, openInit, speed){
    // toggler=class of click target
    // togglee=class of container to expand
    // openInit=true - all togglee open on enter
    // speed=speed of expand/collapse animation
    
    if (openInit === true && openInit !== null) {
        $('.expand').hide();
    }
    else {
        $('.' + togglee).hide();
        $('.collapse').hide();
        utils.resizeFrame();
    }
    $('.' + toggler).click(function(){
        $(this).next('.' + togglee).fadeToggle(speed);
        $(this).find('.expand').toggle();
        $(this).find('.collapse').toggle();
        utils.resizeFrame();
    });
};

/*
 utilities
 */
/*
 initialize a jQuery-UI dialog
 */
utils.setupUtils = function(){
    $('.revealInstructions').click(function(e){
        e.preventDefault();
        $(this).hide().next().fadeIn('fast');
    });
};
utils.startDialog = function(dialogTarget){
    $("#" + dialogTarget).dialog({
        close: function(event, ui){
            utils.resizeFrame('shrink');
        },
        autoOpen: false,
        modal: true,
        height: 330,
        maxHeight: 350,
        width: 500,
        draggable: true,
        closeOnEscape: true
    });
    
};
/*
 position, open a jQuery-UI dialog, adjust the parent iframe size if any
 */
utils.endDialog = function(ev, dialogTarget){
    var frame;
    if (top.location !== self.location) {
        frame = parent.document.getElementById(window.name);
    }
    if (frame) {
        var clientH = document.body.clientHeight + 360;
        $(frame).height(clientH);
    }
    
    $("#" + dialogTarget).dialog('option', 'position', [100, ev.pageY + 10]);
    $("#" + dialogTarget).dialog("open");
    
};


// toggle a fade
jQuery.fn.fadeToggle = function(speed, easing, callback){
    return this.animate({
        opacity: 'toggle'
    }, speed, easing, callback);
};
//escape markup
String.prototype.escapeHTML = function(){
    return (this.replace(/&/g, '&amp;').replace(/>/g, '&gt;').replace(/</g, '&lt;').replace(/"/g, '&quot;'));
};

/*
 resize the iframe based on the contained document height.
 used after DOM operations that add or substract to the doc height
 */
utils.resizeFrame = function(updown){
    var clientH;
    if (top.location !== self.location) {
        var frame = parent.document.getElementById(window.name);
    }
    if (frame) {
        if (updown === 'shrink') {
            clientH = document.body.clientHeight;
        }
        else {
            clientH = document.body.clientHeight + 50;
        }
        $(frame).height(clientH);
    }
    else {
        // throw( "resizeFrame did not get the frame (using name=" + window.name + ")" );
    }
};




var setupCategTools = function(){

    var sorttoolSelectionList = function(){
        var mylist = $('#toolSelectionList ul');
        var listitems = mylist.children('li').get();
        listitems.sort(function(a, b){
            return $(a).text().toUpperCase().localeCompare($(b).text().toUpperCase());
        });
        $.each(listitems, function(idx, itm){
            mylist.append(itm);
        });
        if ($('#toolSelectionList ul li').length > 1) {
            if ($('#toolSelectionList ul').find('li#selected_sakai_home').length) {
             $('#toolSelectionList ul').find('li#selected_sakai_home').insertBefore($('#toolSelectionList ul li:first-child'));
            }
        }
    };
    

   var noTools = function() {
        
        if ($('#toolSelectionList  ul li').length - 1 === 0)  {
            $('#toolSelectionList #toolSelectionListMessage').show();
        }
        else {
            $('#toolSelectionList #toolSelectionListMessage').hide();
        }
};
    var showAlert = function(e){
        var pos = $(e.target).position();
        $(e.target).parent('li').append('<div id=\"alertBox\">Remove configured tool? <a href=\"#\" id=\"alertBoxYes\">Yes</a>&nbsp;|&nbsp;<a href=\"#\" id=\"alertBoxNo\">No</a></div>');
        $(e.target).find('#alertBox').css({
            'top': pos.top - 14,
            'left': pos.left - 150
        });
        $('#alertBox a#alertBoxYes').live('click', function(){
            $(this).parent('div').prev('a').removeClass('toolInstance').click();
            $('#alertBox').remove();
        });
        $('#alertBox a#alertBoxNo').live('click', function(){
            $(this).closest('li').removeClass('highlightTool');
            $('#alertBox').remove();
        });
    };
    
    
    
    var sourceList = $('input[name="selectedTools"][type="checkbox"]');
    $.each(sourceList, function(){
        var removeLink = '';
        var thisToolCat = '';
        var thisIdClass = '';
        var toolInstance = '';
        var thisToolCatEsc = '';
        if ($(this).attr('id').length > 37) {
            thisToolCat = $(this).attr('id').substring(36) + '';
            thisIdClass = $(this).attr('id').substring(36) + '';
            toolInstance = ' toolInstance';
        }
        else {
            thisToolCat = $(this).attr('id') + '';
            thisIdClass = $(this).attr('id') + '';
        }
        thisToolCatEsc = thisToolCat.replace(' ', '_');
        

        if ($(this).attr('disabled') !== true) {
            removeLink = '<a href="#" class=\"removeTool ' + toolInstance + '\">x</a>';
        }
        
        if ($(this).attr('checked')) {
            //console.log(thisToolCat  + ' has a checked tool')
            $(this).next('label').css('font-weight', 'bold');
            $('#toolSelectionList ul').append('<li class=\"icon-' + thisIdClass.replace(/\./g, '-') + '\" id=\"selected_' + $(this).attr('id').replace(/\./g, '_') + '\">' + $(this).next('label').text() + removeLink + '</li>');
            $('#toolHolder').find('#' + thisToolCatEsc).find('ul').show();
            $('#toolHolder').find('#' + thisToolCatEsc).find('h4').find('a').addClass('open');
        }
        else {
            $(this).next('label').css('font-weight', 'normal');
        }
        var parentRow = $(this).closest('li');
        $('#toolHolder').find('#' + thisToolCatEsc).find('ul').append(parentRow);
        //push into an array this id, and to close the function traverse and send a click to each
    });
    $('.toolGroup').each(function(){
        var countChecked = $(this).find(':checked').length;
        var countTotal = $(this).find('input[type="checkbox"]').length;
        if (countChecked === 0) {
            $(this).parent('li').find('#selectAll').show();
            $(this).parent('li').find('#unSelectAll').hide();
        }
        if (countChecked === countTotal) {
            $(this).parent('li').find('#selectAll').hide();
            $(this).parent('li').find('#unSelectAll').show();
        }
        if (countChecked !==  0 && countChecked !== countTotal) {
            $(this).parent('li').find('#selectAll').show();
            $(this).parent('li').find('#unSelectAll').hide();
        }
        $(this).parent('li').find('span.checkedCount').text(countChecked).show(); //$(this).parent('li').find('span.checkedCount').hide();
    });
    

    $('#toolHolder a').click(function(e){
        e.preventDefault();
        if ($(this).attr('href')) {
            $(this).closest('li').find('ul').fadeToggle('fast', function(){
                utils.resizeFrame('grow');
            });
            $(this).toggleClass('open');
            return false;
        }
    });
    
    $('input[name="selectedTools"][type="checkbox"]').click(function(){
        var myId = $(this).attr('id').replace(/\./g, '_');
        if(($(this).closest('ul').find(':checked').length === $(this).closest('ul').find('input[type="checkbox"]').length) && $(this).closest('ul').find(':checked').length > 0) {
            $('#selectAll').hide();
            $('#unSelectAll').show();
        }
        else {
            $('#selectAll').show();
            $('#unSelectAll').hide();
            
        }
        var count = $(this).closest('ul').find(':checked').length;
        $(this).closest('ul').parent('li').find('span.checkedCount').text(count).show();
        var thisIdClass;
        if ($(this).attr('id').length > 37) {
            thisIdClass = $(this).attr('id').substring(36) + '';
        }
        else {
            thisIdClass = $(this).attr('id') + '';
        }
        
        if ($(this).attr('checked')) {
            $(this).next('label').css('font-weight', 'bold');
            $('#toolSelectionList ul').append('<li style=\"display:none\" class=\" highlightTool icon-' + thisIdClass.replace(/\./g, '-') + '\" id=\"selected_' + myId + '\">' + $(this).next('label').text() + '<a href="#" class=\"removeTool\">x</a></li>');
            sorttoolSelectionList();
            $('#toolSelectionList ul').find('#selected_' + myId).fadeIn(2000, function(){
                $(this).removeClass('highlightTool');
            });
            
        }
        else {
            $(this).next('label').css('font-weight', 'normal');
            $('#toolSelectionList ul').find('#selected_' + myId).addClass('highlightTool').fadeOut(1000, function(){
                $(this).remove();
            });
        }
        utils.resizeFrame('grow');
        noTools();
    });
    
    $('#collExpContainer a').click(function(e){
        // elegant - but flawed
        // $('ol#toolHolder h4 a').trigger('click');
        // more involved but sound
        if ($(this).attr('id') === 'expandAll') {
            $('#toolHolder .toolGroup').not(':eq(0)').show();
            $('#toolHolder h4 a').addClass('open');
            utils.resizeFrame('grow');
        }
        else {
            $('#toolHolder .toolGroup').not(':eq(0)').hide();
            $('#toolHolder h4 a').removeClass('open');
            utils.resizeFrame('grow');
        }
        
        // just plain elegant
        $('#collExpContainer a').toggle();
        return false;
    });
    
    $('.selectAll').click(function(){
        if ($(this).attr('id') === "selectAll") {
            $('.sel_unsel_core em').hide();
            $('.sel_unsel_core em#unSelectAll').show();
            
            $.each($(this).closest('li').find('input[type="checkbox"]'), function(){
                var myId = $(this).attr('id').replace(/\./g, '_');
                $('#toolSelectionList ul').append('<li class=\"icon-' + $(this).attr('id').replace(/\./g, '-') + '\" id=\"selected_' + myId + '\">' + $(this).next('label').text() + '<a href="#" class=\"removeTool\">x</a></li>');
            });
            $(this).closest('li').find('label').css('font-weight', 'bold');
            $(this).closest('li').find('input[type="checkbox"]').attr('checked', true);
            utils.resizeFrame('grow');
            sorttoolSelectionList();
        }
        else {
            $('.sel_unsel_core em').hide();
            $('.sel_unsel_core em#selectAll').show();
            $.each($(this).closest('li').find(':checked'), function(){
                var myId = $(this).attr('id').replace(/\./g, '_');
                $('#toolSelectionList ul').find('#selected_' + myId).remove();
            });
            $(this).closest('li').find('input[type="checkbox"]').attr('checked', false);
            $(this).closest('li').find('label').css('font-weight', 'normal');
            utils.resizeFrame('grow');
        }
        $(this).closest('li').find('span.checkedCount').text($(this).closest('li').find(':checked').length).show(); 
    });
    
    
    $('.removeTool').live('click', function(e){
        e.preventDefault();
        var myId = $(this).closest('li').attr('id').replace(/_/g, '.').replace('selected.','');
        if ($('#toolHolder').find('input[type="checkbox"][id=' + myId + ']').attr('disabled') == 'disabled') {
            // there should be no instances of a "required" tool having a control to remove it.
        }
        else {
            $('#toolHolder').find('input[type="checkbox"][id=' + myId + ']').attr('checked', false).next('label').css('font-weight', 'normal');
            if ($(this).hasClass('toolInstance')) {
                $(this).closest('li').addClass('highlightTool');
                showAlert(e);
                return false;
                // remove the checkbox? put in an alert
            }
        }
        $(this).closest('li').addClass('highlightTool').fadeOut('slow', function(){
            $(this).closest('li').remove();
        });
        var countSelected = $('#toolHolder').find('input[type="checkbox"][value=' + myId + ']').closest('ul').find(':checked').length;
        
        
        $('#toolHolder').find('input[type="checkbox"][id=' + myId + ']').closest('ul').closest('li').find('.checkedCount').text(countSelected);
        noTools();
    });
 
    $('.moreInfoTool').click(function(e){
        e.preventDefault();
        $('#moreInfoHolder').html('');
        var thisToolId = '';
        var thisToolTitle = '';
        thisToolTitle = $(this).closest('li').find('label').text();
        
        if ($(this).closest('li').find('input[type="checkbox"]').attr('id').length > 37) {
            thisToolId = $(this).closest('li').find('input[type="checkbox"]').attr('id').substring(36) + '';
        }
        else {
            thisToolId = $(this).closest('li').find('input[type="checkbox"]').attr('id') + '';
        }
        
        //var thistoolIdURL = '/portal/help/TOCDisplay/content.hlp?docId=argq'
        //var XthistoolIdURL = '/portal/help/TOCDisplay/main?help=' + $(this).closest('li').find('input[type="checkbox"]').attr('id');
        //        console.log(XthistoolIdURL)
        //        $("#moreInfoHolder").load(XthistoolIdURL + 'body')
        
          //should be something more robust (turn off cache, fallback callbacks that put in some content 
          // also - what about the url? the path should be settable
          $('#moreInfoHolder').load('/access/content/public/more-info/' + thisToolId);

        $("#moreInfoHolder").dialog({
            autoOpen: false,
            height: 500,
            maxHeight: 500,
            maxWidth: 700,
            width: 700,
            title: thisToolId,
            modal: true
        });
        $("span.ui-dialog-title").text(thisToolTitle);
        $('#moreInfoHolder').dialog('open');
    });
    
};

