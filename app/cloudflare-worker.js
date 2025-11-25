export default {
  async fetch(request) {
    const url = new URL(request.url);

    let count = parseInt(url.searchParams.get("count")) || 50;
    if (count > 200) count = 200;

    const videos = [];
    for (let i = 0; i < count; i++) {
      videos.push({
        id: crypto.randomUUID(),
        url: randomVideo(),
        title: "Video #" + i,
       user: "User" + randomInt(1000),
        likes: randomInt(10_000),
        shares: randomInt(1000),
        reposts: randomInt(500),
        comments: randomComments(),
      });
    }

    return new Response(JSON.stringify(videos), {
      headers: { "content-type": "application/json" }
    });
  }
};

function randomVideo() {
  const samples = [
    "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
    "https://videos.pexels.com/video-files/854152/854152-hd_1280_720_24fps.mp4",
    "https://videos.pexels.com/video-files/855017/855017-hd_1280_720_24fps.mp4"
  ];
  return samples[Math.floor(Math.random() * samples.length)];
}

function randomInt(max) {
  return Math.floor(Math.random() * max);
}

function randomComments(depth = 0) {
  const list = ["Nice!", "🔥🔥🔥", "So cool", "Amazing video", "lol", "Wow", "Hahaha"];
  const commentsCount = randomInt(5);
  return Array.from({ length: commentsCount }, () => ({
    text: list[randomInt(list.length)],
    replies: depth < 2 ? randomComments(depth + 1) : []
  }));
}
