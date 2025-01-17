#!/bin/bash

if [[ -z "$1" ]]
then
    BASE_URL="http://localhost:3000"
else
    BASE_URL="https://${1}.execute-api.eu-north-1.amazonaws.com/dev"
fi

function testCrulRest() {
    login="$1"
    passwd="${2:-passwd}"
    json_file="customer.json"

    if [[ -z "$login" ]]
    then
        opts="-s"
        echo "### Test with no basic auth..."
    else
        opts="-s --user ${login}:${passwd}"
        echo "### Test with ${login} user..."
    fi

    echo "Create a new customer by ${login}..."

    http_status=`curl -X POST \
        ${opts} --output ${json_file} --write-out %{http_code} \
        -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}" \
        "${BASE_URL}/customer/"`

    echo ${http_status}

    if [[ ${http_status} == "201" ]]
    then
        cat ${json_file}|jq "."
        customer_uuid=`jq -r ".id" < ${json_file}`
    fi

    if [[ ${http_status} == "403" ]]
    then
        echo "Create a new customer by admin..."

        http_status=`curl -X POST \
            -s --user admin:passwd \
            --output ${json_file} --write-out %{http_code} \
            -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}" \
            "${BASE_URL}/customer/"`

        cat ${json_file}|jq "."
        customer_uuid=`jq -r ".id" < ${json_file}`
    fi

    echo "Read customer by ${login}..."

    http_status=`curl -X GET \
        ${opts} --output ${json_file} --write-out %{http_code} \
        "${BASE_URL}/customer/${customer_uuid}"`
        
    echo ${http_status}
    cat ${json_file}|jq "."

    echo "Update customer by ${login}..."

    http_status=`curl -X PUT \
        ${opts} --output ${json_file} --write-out %{http_code} \
        -d "{\"id\":\"${customer_uuid}\",\"firstName\":\"Peter\",\"lastName\":\"Pan\",\"email\":\"peter.pan@somewhere.com\"}" \
        "${BASE_URL}/customer/${customer_uuid}"`
        
    echo ${http_status}
    cat ${json_file}|jq "."

    echo "Delete customer by ${login}..."

    http_status=`curl -X DELETE \
        ${opts} --output ${json_file} --write-out %{http_code} \
        "${BASE_URL}/customer/${customer_uuid}"`
        
    echo ${http_status}
    cat ${json_file}|jq "."

    rm ${json_file}
}

echo "BASE_URL: ${BASE_URL}"
testCrulRest admin
testCrulRest user
testCrulRest
