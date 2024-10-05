"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (g && (g = 0, op[0] && (_ = 0)), _) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.MarketplaceClient = void 0;
var anchor_1 = require("@coral-xyz/anchor");
var web3_js_1 = require("@solana/web3.js");
var nodewallet_1 = require("@coral-xyz/anchor/dist/cjs/nodewallet");
var web3_js_2 = require("@solana/web3.js");
var spl_token_1 = require("@solana/spl-token");
// define your private key here
var keypair = web3_js_1.Keypair.fromSecretKey(Uint8Array.from([]));
var MarketplaceClient = /** @class */ (function () {
    /**
     *
     */
    function MarketplaceClient() {
        var _this = this;
        this.counterPublicKey = null;
        this.connection = new web3_js_1.Connection((0, web3_js_1.clusterApiUrl)("testnet"));
        this.provider = new anchor_1.AnchorProvider(this.connection, new nodewallet_1.default(keypair), {});
        // Initialize the counter.
        this.initializeCounter().then(function (counterPublicKey) {
            _this.counterPublicKey = counterPublicKey;
            console.log("Counter account public key:", counterPublicKey.toString());
        }).catch(function (error) {
            console.error("Failed to initialize counter:", error);
        });
    }
    MarketplaceClient.prototype.initializeCounter = function () {
        return __awaiter(this, void 0, void 0, function () {
            var counterAccount, transaction, _a, _b, _c, _d, signedTransaction, serializedTransaction, transactionId;
            var _e;
            return __generator(this, function (_f) {
                switch (_f.label) {
                    case 0:
                        counterAccount = web3_js_1.Keypair.generate();
                        _b = (_a = new web3_js_2.Transaction()).add;
                        _d = (_c = web3_js_2.SystemProgram).createAccount;
                        _e = {
                            fromPubkey: this.provider.wallet.publicKey,
                            newAccountPubkey: counterAccount.publicKey,
                            space: spl_token_1.AccountLayout.span
                        };
                        return [4 /*yield*/, this.connection.getMinimumBalanceForRentExemption(spl_token_1.AccountLayout.span)];
                    case 1:
                        transaction = _b.apply(_a, [_d.apply(_c, [(_e.lamports = _f.sent(),
                                    _e.programId = new web3_js_1.PublicKey('ProgramPublicKey'),
                                    _e)])]);
                        return [4 /*yield*/, this.provider.wallet.signTransaction(transaction)];
                    case 2:
                        signedTransaction = _f.sent();
                        serializedTransaction = signedTransaction.serialize();
                        return [4 /*yield*/, this.connection.sendRawTransaction(serializedTransaction)];
                    case 3:
                        transactionId = _f.sent();
                        return [4 /*yield*/, this.connection.confirmTransaction(transactionId)];
                    case 4:
                        _f.sent();
                        return [2 /*return*/, counterAccount.publicKey || void 0];
                }
            });
        });
    };
    MarketplaceClient.prototype.initializeMarketplace = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                return [2 /*return*/];
            });
        });
    };
    MarketplaceClient.prototype.listNft = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                return [2 /*return*/];
            });
        });
    };
    MarketplaceClient.prototype.listNftInSpl = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                return [2 /*return*/];
            });
        });
    };
    MarketplaceClient.prototype.updatePrice = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                return [2 /*return*/];
            });
        });
    };
    MarketplaceClient.prototype.cancelListing = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                return [2 /*return*/];
            });
        });
    };
    MarketplaceClient.prototype.buyNft = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                return [2 /*return*/];
            });
        });
    };
    MarketplaceClient.prototype.buyNftWithSpl = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                return [2 /*return*/];
            });
        });
    };
    MarketplaceClient.prototype.getMarketplaceMetadata = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                throw "unimplemented!";
            });
        });
    };
    MarketplaceClient.prototype.getUserListings = function (address) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                throw "unimplemented";
            });
        });
    };
    MarketplaceClient.prototype.getAllListings = function () {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                throw "unimplemented";
            });
        });
    };
    MarketplaceClient.prototype.getUserNfts = function (address) {
        return __awaiter(this, void 0, void 0, function () {
            return __generator(this, function (_a) {
                throw "unimplemented";
            });
        });
    };
    return MarketplaceClient;
}());
exports.MarketplaceClient = MarketplaceClient;
