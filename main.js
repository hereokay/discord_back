const mongoose = require('mongoose');
const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
require('dotenv').config();
const rateLimit = require('express-rate-limit');
const requestIp = require('request-ip');



dbUri = process.env.DB_URI


// ----------------------------- VALUE ------------------------------------

const corsOptions = {
  origin: '*',
  optionsSuccessStatus: 200 // 일부 레거시 브라우저에 대한 지원
};

const app = express();
const port = 8000;  // 8000번 api 포트

app.use(cors(corsOptions));
app.use(bodyParser.json());

let blockList = [];

// '/blockList' 경로에 대한 GET 요청 처리
app.get('/blockList', (req, res) => {
  res.status(200).json(blockList);
  
});


app.get('/block', (req, res) => {
  const userName = req.query.userName; // 요청으로부터 userId 받기
  const security = req.query.security;


  if (security !== '0807'){
    console.log(security)
    return res.status(400).send('security가 올바르지 않습니다.');
  }
  if (!userName) {
      return res.status(400).send('userName가 필요합니다.');
  }

  // 블랙리스트에 userId가 이미 존재하는지 확인
  if (!blockList.includes(userName)) {
      blockList.push(userName); // 블랙리스트에 추가
  }

  res.status(200).json({ 
      message: `${userName}가 블랙리스트에 추가되었습니다.`,
      blockList: blockList
  });
});



// POST 요청 처리
app.post('/search', async (req, res) => {
  try {
    const { content, userName, optionSearch,macro } = req.body;


  

    console.log("content: '" +content + "' userName: '" + userName + "' macro: '" + macro + "' IP: '" + requestIp.getClientIp(req)+ "'")
    const contentKeywords = content.split(" ");

    // 각 키워드에 대해 MongoDB 쿼리 조건 생성
    const contentConditions = contentKeywords.map(keyword => {
      return { content: new RegExp(keyword, 'i') };
    });
    
    let query = {
      $and: [
          ...contentConditions,
          { globalName: new RegExp(userName, 'i') },
          { content: new RegExp(optionSearch, 'i') }
      ]
  };

  const results = await History.find(query).sort({ _id: -1 }).limit(2000);  
  
  // BlockList에 없는 userName을 가진 원소만 필터링
    if (macro===true){
    const filteredResults = results.filter(item => !blockList.includes(item.userName));
    res.status(200).json(filteredResults);
    }
    else{
      res.status(200).json(results);
    }
  } catch (error) {
    console.log(error.message)
    res.status(500).send('서버 오류: ' + error.message);
  }
});


app.listen(port, '0.0.0.0');


const userSchema = new mongoose.Schema({
  globalName: String,
  userName: String,
  content: String,
  guildId:String,
  channelId:String,
  msgId:String,
  timeStamp: String
});

const History = mongoose.model('history', userSchema);



// ----------------------------- CONNECT ------------------------------------

// MongoDB 데이터베이스 연결 설정 15.164.105.119
mongoose.connect(dbUri, {
  useNewUrlParser: true,
  useUnifiedTopology: true
});


// DB 연결관리
const db = mongoose.connection;

db.on('error', console.error.bind(console, 'MongoDB 연결 에러:'));

db.once('open', function() {
  console.log('MongoDB 연결 성공');
});

