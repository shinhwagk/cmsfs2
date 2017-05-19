#prefix: p_ production.
param (
  [Parameter(Mandatory = $true)][string]$p_host,
  [Parameter(Mandatory = $true)][Int]$p_port,
  [Parameter(Mandatory = $true)][string]$p_user
)

$project_home = ".."
$project_name = "cmsfs2"

$deploy_dir = "${project_home}/deploy"
$remote_deploy_dir = "/opt/${project_name}"

# ./gradlew.bat assemble

function scpFile([bool]$isFile?, [string]$source_file, [string]$target_path) {
  if ($isFile) {
    scp $source_file "${p_user}@${p_host}:${target_path}"
  }
  else {
    scp -r $source_file "${p_user}@${p_host}:${target_path}"
  }
}

function scpCmsfsProject() {
  $source = "${project_home}/build/distributions/${project_name}.tar"
  $target = "${remote_deploy_dir}/cmsfs2"
  scpFile $true $source $target
}

function updateDockerCompose() {
  scpFile $true "${deploy_dir}/docker-compose.yml" $remote_deploy_dir 
}

function updateNginx(){
    scpFile $false "${deploy_dir}/nginx" $remote_deploy_dir 
}

function executeCommand([string]$command){
    ssh root@$p_host "${command}"
}

updateDockerCompose
# updateNginx
scpCmsfsProject


executeCommand "cd /opt/cmsfs2; docker-compose stop cmsfs"
executeCommand "cd /opt/cmsfs2; docker-compose rm -f cmsfs"
executeCommand "cd /opt/cmsfs2/cmsfs2; sh build.sh"
executeCommand "cd /opt/cmsfs2; docker-compose up -d cmsfs"