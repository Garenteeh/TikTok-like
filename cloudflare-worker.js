export default {
  async fetch(request) {
    const url = new URL(request.url);

    let count = parseInt(url.searchParams.get("count")) || 50;
    if (count > 200) count = 200;

    const videos = [];

    for (let i = 0; i < count; i++) {
      videos.push({
        id: crypto.randomUUID(),
        url: await randomVideo(),
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

const PEXELS_API_KEY = "FVrEEoR0dCL23mf7TCSZu4SnkuLpflJsh3jnoE93idllK3rV6gZr3MT0";

async function randomVideo() {
  const queries = ["people", "fun", "nature", "city", "technology"];
  const query = queries[randomInt(queries.length)];
  const page = randomInt(10) + 1;

  try {
    const res = await fetch(
      `https://api.pexels.com/videos/search?query=${query}&per_page=5&page=${page}`,
      {
        headers: {
          Authorization: PEXELS_API_KEY
        }
      }
    );

    const data = await res.json();

    if (!data.videos || data.videos.length === 0) {
      return fallbackVideo();
    }

    const video = data.videos[randomInt(data.videos.length)];

    const file =
      video.video_files.find(v => v.quality === "sd") ||
      video.video_files.find(v => v.file_type === "video/mp4") ||
      video.video_files[0];

    return file.link;

  } catch (e) {
    return fallbackVideo();
  }
}

function fallbackVideo() {
  const samples = [
    "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
  ];
  return samples[randomInt(samples.length)];
}

function randomInt(max) {
  return Math.floor(Math.random() * max);
}

function randomComments(depth = 0) {
  const list = ["Nice!", "🔥🔥🔥", "So cool", "Amazing video", "lol", "Wow", "Hahaha"];
  const users = ["Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry"];
  const commentsCount = randomInt(5);

  return Array.from({ length: commentsCount }, () => {
    const now = Date.now();
    const randomPastTime = now - randomInt(7 * 24 * 60 * 60 * 1000);

    return {
      id: crypto.randomUUID(),
      message: list[randomInt(list.length)],
      user: users[randomInt(users.length)],
      timestamp: randomPastTime,
      likes: randomInt(100),
      isLiked: false,
      replies: depth < 2 ? randomComments(depth + 1) : []
    };
  });
}
