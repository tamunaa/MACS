// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;
// 0x5B38Da6a701c568545dCfcB03FcB875f56beddC4
// 0xAb8483F64d9C6d1EcF9b849Ae677dD3315835cb2
// 0x4B20993Bc481177ec7E8f571ceCaE8A9e22C02db 
// testting accounts on remix
// ["0x5B38Da6a701c568545dCfcB03FcB875f56beddC4", "0xAb8483F64d9C6d1EcF9b849Ae677dD3315835cb2", "0x4B20993Bc481177ec7E8f571ceCaE8A9e22C02db", "0x5B38Da6a701c568545dCfcB03FcB875f56beddC4"]

contract Splitwise {
    struct MoneyRequest {
        uint256 requestId;
        address requester;
        address debtor;
        uint256 amount;
        bool isPaid;
        bool isRejected;
    }

    constructor() payable {}
    receive() external payable{}

    //yvela requesti
    MoneyRequest[] public moneyRequests;
    uint256 public nxtRequestId = 0;

    //misamarti --> reqvestis id --> actiuria tu ara
    mapping(address => mapping(uint256 => bool)) public sentRequests;
    mapping(address => mapping(uint256 => bool)) public receivedRequests;

    
    mapping(address => mapping(address => uint256)) public debts;

    address[] public users;

    event MoneyRequestAdded(uint256 requestId, address requester, address debtor, uint256 amount);
    event MoneyRequestPaid(uint256 requestId, address payer, address debtor, uint256 amount);
    event MoneyRequestRejected(uint256 requestId, address requester, address debtor, uint256 amount);


    function isParticipant(address participant) internal view returns (bool) {
        for (uint256 i = 0; i < users.length; i++) {
            if (users[i] == participant) {
                return true;
            }
        }
        return false;
    }

    //adding new users
    function addNode(address add) private {
        if (!isParticipant(add)) {
            users.push(add);
        }
    }

    function submitMoneyRequest(address debtor, uint256 amount) public{
        require(amount > 0, "Amount must be greater than zero");

        uint256 curRequestId = nxtRequestId;

        MoneyRequest memory newRequest = MoneyRequest({
            requestId: nxtRequestId,
            requester: msg.sender,
            debtor: debtor,
            amount: amount,
            isPaid: false,
            isRejected: false
        });

        moneyRequests.push(newRequest);
        sentRequests[msg.sender][curRequestId] = true;
        receivedRequests[debtor][curRequestId] = true;

        nxtRequestId++;

        addNode(debtor);
        addNode(msg.sender);
        debts[msg.sender][debtor] += amount;

        emit MoneyRequestAdded(curRequestId, msg.sender, debtor, amount);
    }

    function getReceivedRequests() public view returns (MoneyRequest[] memory){
        uint256 receivedCount = 0;
        for (uint256 i = 0; i < moneyRequests.length; i++) {
            if (moneyRequests[i].debtor == msg.sender && !moneyRequests[i].isPaid && !moneyRequests[i].isRejected) {
                receivedCount++;
            }
        }

        MoneyRequest[] memory received = new MoneyRequest[](receivedCount);
        uint256 currentIndex = 0;

        for (uint256 i = 0; i < moneyRequests.length; i++) {
            if (moneyRequests[i].debtor == msg.sender && !moneyRequests[i].isPaid && !moneyRequests[i].isRejected) {
                received[currentIndex] = moneyRequests[i];
                currentIndex++;
            }
        }
        return received;
    }

    function getSentRequests() public view returns (MoneyRequest[] memory) {
        uint256 sentCount = 0;
        for (uint256 i = 0; i < moneyRequests.length; i++) {
            if (moneyRequests[i].requester == msg.sender && !moneyRequests[i].isPaid && !moneyRequests[i].isRejected) {
                sentCount++;
            }
        }

        MoneyRequest[] memory sent = new MoneyRequest[](sentCount);

        for (uint256 i = 0; i < moneyRequests.length; i++) {
            if (moneyRequests[i].requester == msg.sender && !moneyRequests[i].isPaid && !moneyRequests[i].isRejected) {
                sent[i] = moneyRequests[i];
            }
        }

        return sent;
    }

    function getParticipatingAddresses() public view returns (address[] memory) {
        return users;
    }

    function getNodes() public view returns (address[] memory) {
        return users;
    }


    function payToAddress(address person) external payable {
        MoneyRequest[] memory tmpsentRequests = getReceivedRequests();

        for(uint256 i = 0; i < tmpsentRequests.length; i++){
            uint256 requestId = tmpsentRequests[i].requestId;
            MoneyRequest storage request = moneyRequests[requestId];

            if(request.requester != person){
                continue;
            }

            if(request.isPaid || request.isRejected){
                continue;
            }

            payable(request.requester).transfer(request.amount);

            request.isPaid = true;
            sentRequests[request.requester][requestId] = false;
            receivedRequests[request.debtor][requestId] = false;

            debts[request.requester][request.debtor] -= request.amount;
        }       
    }


    function payForAllTheRequests() external payable {
        MoneyRequest[] memory tmpRequests = getReceivedRequests();

        for(uint256 i = 0; i < tmpRequests.length; i++){
            uint256 requestId = tmpRequests[i].requestId;
            MoneyRequest storage request = moneyRequests[requestId];

            if(request.isPaid || request.isRejected){
                continue;
            }

            payable(request.requester).transfer(request.amount);

            request.isPaid = true;
            sentRequests[request.requester][requestId] = false;
            receivedRequests[request.debtor][requestId] = false;

            debts[request.requester][request.debtor] -= request.amount;
        }       
    }

    //ixdis tranzaqcias
    function payForRequestedAmount(uint256 requestId) external payable {
        MoneyRequest storage request = moneyRequests[requestId];
        require(request.debtor == msg.sender, "Only the debtor can pay for the request");
        require(!request.isPaid && !request.isRejected, "Request has already been paid or rejected");

        payable(request.requester).transfer(request.amount);
        
        request.isPaid = true;
        sentRequests[request.requester][requestId] = false;
        receivedRequests[request.debtor][requestId] = false;

        debts[request.requester][request.debtor] -= request.amount;

        emit MoneyRequestPaid(requestId, msg.sender, request.debtor, request.amount);
    }

    function rejectMoneyRequest(uint256 requestId) public{
        MoneyRequest storage request = moneyRequests[requestId];
        require(request.debtor == msg.sender, "Only the debtor can reject the request");
        require(!request.isPaid && !request.isRejected, "Request has already been paid or rejected");

        request.isRejected = true;
        sentRequests[request.requester][requestId] = false;
        receivedRequests[request.debtor][requestId] = false;

        emit MoneyRequestRejected(requestId, request.requester, request.debtor, request.amount);
    }


    function resolveCycle(
        address[] memory cyclePath
    ) external {
        require(isValidCycle(cyclePath), "Invalid cycle path provided");
        processCycle(cyclePath);
    }

    function isValidCycle(address[] memory cyclePath) private view returns (bool) {
        require(cyclePath.length >= 2, "Cycle path must have at least two nodes");
        for (uint256 i = 0; i < cyclePath.length - 1; i++) {
            require(debts[cyclePath[i]][cyclePath[i + 1]] > 0, "Invalid edge in cycle path");
        }
        return true;
    }

    function processCycle(address[] memory cyclePath) public {
        uint256 minEdgeValue = type(uint256).max;

        for (uint256 i = 0; i < cyclePath.length - 1; i++) {
            uint256 edgeValue = debts[cyclePath[i]][cyclePath[i + 1]];

            require(edgeValue > 0, "Invalid edge in cycle path");

            if (edgeValue < minEdgeValue) {
                minEdgeValue = edgeValue;
            }
        }

        for (uint256 i = 0; i < cyclePath.length - 1; i++) {
            debts[cyclePath[i]][cyclePath[i + 1]] -= minEdgeValue;
        }
    }

    function getNeighbors(address node) public view returns (address[] memory) {
        address[] memory neighbors = new address[](users.length);
        uint count = 0;
        for (uint i = 0; i < users.length; i++) {
            if (debts[node][users[i]] > 0) {
                neighbors[count] = users[i];
                count++;
            }
        }
        assembly { mstore(neighbors, count) }
        return neighbors;
    }

    function cancelMoneyRequest() public {
        MoneyRequest[] memory tmpsentRequests = getSentRequests();
        for (uint256 i = 0; i < tmpsentRequests.length; i++) {
            uint256 requestId = tmpsentRequests[i].requestId;

            MoneyRequest storage request = moneyRequests[requestId];
            require(request.requester == msg.sender, "Only the requester can cancel the request");
            debts[request.requester][request.debtor] -= request.amount; //updaing debts

            request.isRejected = true;
            sentRequests[request.requester][requestId] = false;
            receivedRequests[request.debtor][requestId] = false;        
        }
    }


    function contains(address[] memory array, address addr) internal pure returns (bool) {
        for (uint256 i = 0; i < array.length; i++) {
            if (array[i] == addr) {
                return true;
            }
        }
        return false;
    }


    function getAllCreditors2() external view returns (address[] memory) {
        address[] memory creditors;
        uint256 currentIndex = 0;
        
        for (uint256 i = 0; i < moneyRequests.length; i++) {
            MoneyRequest memory request = moneyRequests[i];
            
            if (!request.isPaid && !request.isRejected && request.debtor == msg.sender) {
                if(contains(creditors, request.requester)){
                    continue;
                }
                creditors[currentIndex] = request.requester;
                currentIndex++;
            }
        }

        return creditors;
    }

    function getAllCreditors() external view returns (address[] memory) {
        uint256 creditorCount = 0;
        for (uint256 i = 0; i < moneyRequests.length; i++) {
            MoneyRequest memory request = moneyRequests[i];

            if (!request.isPaid && !request.isRejected && request.debtor == msg.sender) {
                creditorCount++;
            }
        }

        address[] memory debtors = new address[](creditorCount);
        uint256 currentIndex = 0;

        for (uint256 i = 0; i < moneyRequests.length; i++) {
            MoneyRequest memory request = moneyRequests[i];

            if (!request.isPaid && !request.isRejected && request.debtor == msg.sender) {
                if (contains(debtors, request.debtor)) {
                    continue;
                }
                debtors[currentIndex] = request.debtor;
                currentIndex++;
            }
        }

        return debtors;
    }

    function getAllDebtors() external view returns (address[] memory) {
        uint256 debtorCount = 0;
        for (uint256 i = 0; i < moneyRequests.length; i++) {
            MoneyRequest memory request = moneyRequests[i];

            if (!request.isPaid && !request.isRejected && request.requester == msg.sender) {
                debtorCount++;
            }
        }

        address[] memory debtors = new address[](debtorCount);
        uint256 currentIndex = 0;

        for (uint256 i = 0; i < moneyRequests.length; i++) {
            MoneyRequest memory request = moneyRequests[i];

            if (!request.isPaid && !request.isRejected && request.requester == msg.sender) {
                if (contains(debtors, request.debtor)) {
                    continue;
                }
                debtors[currentIndex] = request.debtor;
                currentIndex++;
            }
        }

        return debtors;
    }


    function getTotalAmountOwed() external view returns (uint256) {
        uint256 totalAmountOwed = 0;
        for (uint256 i = 0; i < users.length; i++) {
            address debtor = users[i];
            totalAmountOwed += debts[debtor][msg.sender];
        }
        return totalAmountOwed;
    }


    function getTotalAmountRequested() external view returns (uint256) {
        uint256 totalRequested = 0;
        for (uint256 i = 0; i < users.length; i++) {
            address debtor = users[i];
            totalRequested += debts[msg.sender][debtor];
        }
        return totalRequested;
    }

    
    function getAmountOwedTo(address debtor) external view returns (uint256) {
        return debts[msg.sender][debtor];
    }

    function getAmountRequestedFrom(address requestor) external view returns (uint256) {
        return debts[requestor][msg.sender];
    }
}
