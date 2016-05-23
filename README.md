<html lang="en" class=" is-copy-enabled">
<head>
<meta charset='utf-8'>

<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Content-Language" content="en">
<meta name="viewport" content="width=1020">

<link rel="canonical" href="https://github.com/motech-implementations/ebodac/master/README.md" data-pjax-transient>
</head>


<body class="logged-out env-production windows vis-public page-blob">

<article class="markdown-body entry-content" itemprop="text">
<h2>Synopsis</h2>

<b>EBODAC</b> is an open source project created for Clinical Trials, built on the MOTECH platform. It has been deployed for an <b>EBOLA Vaccine Trial</b> to run in Sierra Leone.</br>
More information on the MOTECH platform is available at <a href="http://motechproject.org">the MOTECH Project Website</a>. 

</article>
<article class="markdown-body entry-content" itemprop="text">
<h2>Motivation</h2>
This project provides a framework for implementing clinical trials on the MOTECH Platform. Project partners include the <a href="http://www.grameenfoundation.org/">Grameen Foundation</a> and <a href="www.soldevelo.com">SolDevelo</a>. The EBODAC project is a specific implemntation of MOTECH, allowing for information collection, scheduling and tracking of randomized clinical vaccine trials. 
</article>
<article class="markdown-body entry-content" itemprop="text">
<h2>Installation (Linux / Mac / Windows)  </h2>
<h3>Motech Platform Installation</h3>


To install <b>EBODAC</b>, you need to first <a href="http://docs.motechproject.org/en/latest/get_started/installing.html">install the MOTECH Platform</a>. Note that EBODAC is built for MOTECH v 0.27.X.

Developer documentation is <a href="http://docs.motechproject.org/en/latest/development/dev_setup/dev_install.html">also available here</a>, if you'd prefer to build MOTECH yourself.

<h3>EBODAC Module Dependencies</h3> 

The EBODAC module depends on the MOTECH SMS, IVR, Message-Campaign and CSD modules. You can install them from the Manage Modules section on MOTECH's admin page. Alternatively, you can build each module as described below:

<pre>
$ git clone https://github.com/motech/modules.git -b 0.27.x --single-branch
</pre>

After cloning the repository, you'll need to enter the modules directory and build each module.

<pre>
$ cd modules
</pre>

SMS module:
<pre>
$ cd sms
$mvn clean install -Dmaven.test.skip=true
</pre>

IVR module:
<pre>
$ cd ../ivr
$ mvn clean install -Dmaven.test.skip=true
</pre>

Message-Campaign module:
<pre>
$ cd ../message-campaign
$ mvn clean install -Dmaven.test.skip=true
</pre>

Care Services Discovery module:
<pre>
$ cd ../csd
$ mvn clean install -Dmaven.test.skip=true
</pre>

<h3>EBODAC Module Installation</h3>

The EBODAC module can be found in our nexus repository. The easiest way to install it is to <a href="http://nexus.motechproject.org/service/local/artifact/maven/redirect?r=releases&g=org.motechproject&a=ebodac&v=RELEASE&e=jar">download the .jar file</a> into the .motech/bundles directory and restart tomcat. If you choose to build the module yourself, it's located in the motech-implementations GitHub repository. Return to your home directory and clone the repo.

<pre>
$ git clone https://github.com/motech-implementations/ebodac.git
</pre>

Enter the EBODAC folder and install the module:
<pre>
$ cd ebodac
$ mvn clean install -Dmaven.test.skip=true
</pre>

Once complete, you'll want to copy your .motech/bundles directory into your production environment and restart tomcat.
</article>

<article class="markdown-body entry-content" itemprop="text">
<h2>Tests</h2>


Unit and Integration tests can be found at:
<ul>
<li><a href="https://github.com/motech-implementations/ebodac/tree/master/ebodac/src/test/java/org/motechproject/ebodac">EBODAC Unit Tests & Integration Tests</a></li>
<li><a href="https://github.com/motech-implementations/ebodac/tree/master/booking-app/src/test">Booking App Unit Tests and Integration Tests</a></li>
<li><a href="https://github.com/motech-implementations/ebodac/tree/master/ebodac/src/test/java/org/motechproject/ebodac/uitest">UI Tests</a></li>
</ul>
</article>

<article class="markdown-body entry-content" itemprop="text">
<h2>Contributing</h2>

We welcome contributions from the open source community. For instructions on how to get started as a MOTECH contributor, please check out the <a href="http://docs.motechproject.org/en/latest/contribute/index.html">Contribute</a> section of our documentation.

</article>
<article class="markdown-body entry-content" itemprop="text">
<h2>Disclaimer Text Required By GF Legal Team</h2>

Third party technology may be necessary for use of MOTECH. This agreement does not modify or abridge any rights or obligations you have in open source technology under applicable open source licenses.</br>

Open source technology programs that are separate from MOTECH are provided as a courtesy to you and are licensed solely under the relevant open source license.</br>
Any distribution by you of code licensed under an open source license, whether alone or with MOTECH, must be under the applicable open source license.

</article>

</body>
</html>
