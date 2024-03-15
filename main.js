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

async function deleteEntitiesWithKeyword(keyword) {
  try {
    const result = await History.deleteMany({ content: new RegExp(keyword, 'i') });
    console.log(result.deletedCount)
    return result.deletedCount;
  } catch (error) {
    return -1
  }
}


app.get('/delete', async (req, res) => {
  const keyword = req.query.keyword; // 요청으로부터 userId 받기
  const security = req.query.security;


  if (security !== '0807'){
    console.log(security)
    return res.status(400).send('security가 올바르지 않습니다.');
  }
  if (!keyword) {
      return res.status(400).send('keyword가 필요합니다.');
  }

  result = await deleteEntitiesWithKeyword(keyword).catch(console.error);
  if (result === -1){
    res.status(400).json({ 
      message : "메시지 삭제 실패"
    })
  }
  else{
    res.status(200).json({ 
      message: `${keyword}가 포함된 메시지 ${result}개의 메시지가 삭제되었습니다.`
  });
  }

  
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



app.post('/search', async (req, res) => {
  try {
    const { content, userName, optionSearch, macro } = req.body;

    console.log(new Date().toISOString() + " content: '" + content + "' userName: '" + userName + "' macro: '" + macro + "' IP: '" + requestIp.getClientIp(req) + "'");
    
    const contentKeywords = content.split(" ");
    const contentConditions = contentKeywords.map(keyword => {
      return { content: new RegExp(keyword, 'i') };
    });

    // blockList를 고려하여 globalName 필드에 대한 조건 추가
    let userNameCondition = {};
    if (macro === true) {
      userNameCondition = {
        userName: { $nin: blockList, $regex: new RegExp(userName, 'i') }
      };
    } else {
      userNameCondition = {
        userName: new RegExp(userName, 'i')
      };
    }

    let query = {
      $and: [
        ...contentConditions,
        userNameCondition,
        { content: new RegExp(optionSearch, 'i') }
      ]
    };

    // MongoDB 쿼리 실행, 결과는 자동으로 blockList를 고려하여 필터링됨
    const results = await History.find(query).sort({ _id: -1 }).limit(2000);
    

    
    // 중복 제거 로직은 변경 없음
    const notDupResult = results.reduce((acc, current) => {
      if (!acc.some(result => result.content === current.content)) {
        acc.push(current);
      }
      return acc;
    }, []);

    

    // 이전에는 여기에서 blockList를 처리했으나, 쿼리 단계에서 이미 처리하므로 필요 없음
    res.status(200).json(notDupResult);

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

