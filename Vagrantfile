Vagrant.configure("2") do |config|

  if Vagrant.has_plugin?("vagrant-proxyconf")
    config.proxy.http     = "http://139.25.23.169:3128/"
    config.proxy.https    = "http://139.25.23.169:3128/"
    config.proxy.no_proxy = "localhost,127.0.0.1"
  end

  config.vm.provider "virtualbox" do |v|
    v.memory = 3000
    v.cpus = 2
  end

  config.vm.box = "ubuntu/trusty64"
  config.vm.synced_folder "./services", "/services", create: true
  config.vm.network "forwarded_port", guest: 8080, host: 18080
  config.vm.network "forwarded_port", guest: 8081, host: 18081
  config.vm.network "forwarded_port", guest: 8761, host: 18761

  config.vm.provision :docker
  config.vm.provision :docker_compose, yml: "/vagrant/docker/docker-compose.yml", run: "always"
end